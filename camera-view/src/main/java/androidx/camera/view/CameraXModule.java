/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.camera.view;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Rational;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysis.Analyzer;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCapture.OnImageCapturedCallback;
import androidx.camera.core.ImageCapture.OnImageSavedCallback;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.core.UseCase;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCapture.OnVideoSavedCallback;
import androidx.camera.core.impl.LensFacingConverter;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.core.impl.utils.CameraOrientationUtil;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.CameraView.CaptureMode;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static androidx.camera.core.ImageCapture.FLASH_MODE_OFF;

//TODO: [SOUP] START
//TODO: [SOUP] END

/** CameraX use case operation built on @{link androidx.camera.core}. */
final class CameraXModule {
    public static final String TAG = "CameraXModule";

    private static final float UNITY_ZOOM_SCALE = 1f;
    private static final float ZOOM_NOT_SUPPORTED = UNITY_ZOOM_SCALE;
    private static final Rational ASPECT_RATIO_16_9 = new Rational(16, 9);
    private static final Rational ASPECT_RATIO_4_3 = new Rational(4, 3);
    private static final Rational ASPECT_RATIO_9_16 = new Rational(9, 16);
    private static final Rational ASPECT_RATIO_3_4 = new Rational(3, 4);

    private final Preview.Builder mPreviewBuilder;
    private final VideoCaptureConfig.Builder mVideoCaptureConfigBuilder;
    private final ImageCapture.Builder mImageCaptureBuilder;
    private final CameraView mCameraView;
    final AtomicBoolean mVideoIsRecording = new AtomicBoolean(false);
    private CameraView.CaptureMode mCaptureMode = CaptureMode.IMAGE;
    private long mMaxVideoDuration = CameraView.INDEFINITE_VIDEO_DURATION;
    private long mMaxVideoSize = CameraView.INDEFINITE_VIDEO_SIZE;
    @ImageCapture.FlashMode
    private int mFlash = FLASH_MODE_OFF;
    @Nullable
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
            Camera mCamera;
    @Nullable
    private ImageCapture mImageCapture;
    @Nullable
    private VideoCapture mVideoCapture;
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
    @Nullable
    Preview mPreview;
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
    @Nullable
    LifecycleOwner mCurrentLifecycle;
    private final LifecycleObserver mCurrentLifecycleObserver =
            new LifecycleObserver() {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                public void onDestroy(LifecycleOwner owner) {
                    if (owner == mCurrentLifecycle) {
                        clearCurrentLifecycle();
                        mPreview.setSurfaceProvider(null);
                    }
                }
            };
    @Nullable
    private LifecycleOwner mNewLifecycle;
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
    @Nullable
    Integer mCameraLensFacing = CameraSelector.LENS_FACING_BACK;
    @SuppressWarnings("WeakerAccess") /* synthetic accessor */
    @Nullable
    ProcessCameraProvider mCameraProvider;

    //TODO: [SOUP] START
    private final ImageAnalysis.Builder mImageAnalysisBuilder;
    @Nullable
    private ImageAnalysis mImageAnalysis;
    @Nullable
    private ImageAnalysis.Analyzer mImageAnalysisAnalyzer;
    //TODO: [SOUP] END

    CameraXModule(CameraView view) {
        mCameraView = view;

        Futures.addCallback(ProcessCameraProvider.getInstance(view.getContext()),
                new FutureCallback<ProcessCameraProvider>() {
                    // TODO(b/124269166): Rethink how we can handle permissions here.
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(@Nullable ProcessCameraProvider provider) {
                        Preconditions.checkNotNull(provider);
                        mCameraProvider = provider;
                        if (mCurrentLifecycle != null) {
                            bindToLifecycle(mCurrentLifecycle);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        throw new RuntimeException("CameraX failed to initialize.", t);
                    }
                }, CameraXExecutors.mainThreadExecutor());

        mPreviewBuilder = new Preview.Builder().setTargetName("Preview");

        mImageCaptureBuilder = new ImageCapture.Builder().setTargetName("ImageCapture");

        mVideoCaptureConfigBuilder =
                new VideoCaptureConfig.Builder().setTargetName("VideoCapture");

        //TODO: [SOUP] START
        mImageAnalysisBuilder =
                new ImageAnalysis.Builder().setTargetName("ImageAnalysis");
        //TODO: [SOUP] END
    }

    @RequiresPermission(permission.CAMERA)
    void bindToLifecycle(LifecycleOwner lifecycleOwner) {
        mNewLifecycle = lifecycleOwner;

        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            bindToLifecycleAfterViewMeasured();
        }
    }

    @RequiresPermission(permission.CAMERA)
    void bindToLifecycleAfterViewMeasured() {
        if (mNewLifecycle == null) {
            return;
        }

        clearCurrentLifecycle();
        mCurrentLifecycle = mNewLifecycle;
        mNewLifecycle = null;
        if (mCurrentLifecycle.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            mCurrentLifecycle = null;
            throw new IllegalArgumentException("Cannot bind to lifecycle in a destroyed state.");
        }

        if (mCameraProvider == null) {
            // try again once the camera provider is no longer null
            return;
        }

        Set<Integer> available = getAvailableCameraLensFacing();

        if (available.isEmpty()) {
            Log.w(TAG, "Unable to bindToLifeCycle since no cameras available");
            mCameraLensFacing = null;
        }

        // Ensure the current camera exists, or default to another camera
        if (mCameraLensFacing != null && !available.contains(mCameraLensFacing)) {
            Log.w(TAG, "Camera does not exist with direction " + mCameraLensFacing);

            // Default to the first available camera direction
            mCameraLensFacing = available.iterator().next();

            Log.w(TAG, "Defaulting to primary camera with direction " + mCameraLensFacing);
        }

        // Do not attempt to create use cases for a null cameraLensFacing. This could occur if
        // the user explicitly sets the LensFacing to null, or if we determined there
        // were no available cameras, which should be logged in the logic above.
        if (mCameraLensFacing == null) {
            return;
        }

        // Set the preferred aspect ratio as 4:3 if it is IMAGE only mode. Set the preferred aspect
        // ratio as 16:9 if it is VIDEO or MIXED mode. Then, it will be WYSIWYG when the view finder
        // is in CENTER_INSIDE mode.

        boolean isDisplayPortrait = getDisplayRotationDegrees() == 0
                || getDisplayRotationDegrees() == 180;

        Rational targetAspectRatio;
        if (getCaptureMode() == CaptureMode.IMAGE) {
            mImageCaptureBuilder.setTargetAspectRatio(AspectRatio.RATIO_4_3);
            targetAspectRatio = isDisplayPortrait ? ASPECT_RATIO_3_4 : ASPECT_RATIO_4_3;
        } else {
            mImageCaptureBuilder.setTargetAspectRatio(AspectRatio.RATIO_16_9);
            targetAspectRatio = isDisplayPortrait ? ASPECT_RATIO_9_16 : ASPECT_RATIO_16_9;
        }

        mImageCaptureBuilder.setTargetRotation(getDisplaySurfaceRotation());
        mImageCapture = mImageCaptureBuilder.build();

        mVideoCaptureConfigBuilder.setTargetRotation(getDisplaySurfaceRotation());
        mVideoCapture = mVideoCaptureConfigBuilder.build();

        // Adjusts the preview resolution according to the view size and the target aspect ratio.
        int height = (int) (getMeasuredWidth() / targetAspectRatio.floatValue());
        mPreviewBuilder.setTargetResolution(new Size(getMeasuredWidth(), height));

        //TODO: [SOUP] START
        mImageAnalysisBuilder.setTargetAspectRatioCustom(targetAspectRatio);
        mImageAnalysisBuilder.setTargetResolution(new Size(getMeasuredWidth(), height));
        mImageAnalysisBuilder.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST);
        mImageAnalysisBuilder.setTargetRotation(getDisplaySurfaceRotation());
        mImageAnalysis = mImageAnalysisBuilder.build();
        //TODO: [SOUP] END

        mPreview = mPreviewBuilder.build();
        mPreview.setSurfaceProvider(mCameraView.getPreviewView().getPreviewSurfaceProvider());

        CameraSelector cameraSelector =
                new CameraSelector.Builder().requireLensFacing(mCameraLensFacing).build();
        if (getCaptureMode() == CaptureMode.IMAGE) {
            mCamera = mCameraProvider.bindToLifecycle(mCurrentLifecycle, cameraSelector,
                    //TODO: [SOUP] START
                    mImageAnalysis,
                    //TODO: [SOUP] END
                    mImageCapture,
                    mPreview);
        } else if (getCaptureMode() == CaptureMode.VIDEO) {
            mCamera = mCameraProvider.bindToLifecycle(mCurrentLifecycle, cameraSelector,
                    mVideoCapture,
                    mPreview);
        } else {
            mCamera = mCameraProvider.bindToLifecycle(mCurrentLifecycle, cameraSelector,
                    //TODO: [SOUP] START
                    mImageAnalysis,
                    //TODO: [SOUP] END
                    mImageCapture,
                    mVideoCapture, mPreview);
        }

        setZoomRatio(UNITY_ZOOM_SCALE);
        mCurrentLifecycle.getLifecycle().addObserver(mCurrentLifecycleObserver);
        // Enable flash setting in ImageCapture after use cases are created and binded.
        setFlash(getFlash());
    }

    public void open() {
        throw new UnsupportedOperationException(
                "Explicit open/close of camera not yet supported. Use bindtoLifecycle() instead.");
    }

    public void close() {
        throw new UnsupportedOperationException(
                "Explicit open/close of camera not yet supported. Use bindtoLifecycle() instead.");
    }

    public void takePicture(Executor executor, OnImageCapturedCallback callback) {
        if (mImageCapture == null) {
            return;
        }

        if (getCaptureMode() == CaptureMode.VIDEO) {
            throw new IllegalStateException("Can not take picture under VIDEO capture mode.");
        }

        if (callback == null) {
            throw new IllegalArgumentException("OnImageCapturedCallback should not be empty");
        }

        mImageCapture.takePicture(executor, callback);
    }

    public void takePicture(File saveLocation, Executor executor, OnImageSavedCallback callback) {
        if (mImageCapture == null) {
            return;
        }

        if (getCaptureMode() == CaptureMode.VIDEO) {
            throw new IllegalStateException("Can not take picture under VIDEO capture mode.");
        }

        if (callback == null) {
            throw new IllegalArgumentException("OnImageSavedCallback should not be empty");
        }

        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        metadata.setReversedHorizontal(
                mCameraLensFacing != null && mCameraLensFacing == CameraSelector.LENS_FACING_FRONT);
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(saveLocation).setMetadata(
                        metadata).build();
        mImageCapture.takePicture(outputFileOptions, executor, callback);
    }

    public void startRecording(File file, Executor executor, final OnVideoSavedCallback callback) {
        if (mVideoCapture == null) {
            return;
        }

        if (getCaptureMode() == CaptureMode.IMAGE) {
            throw new IllegalStateException("Can not record video under IMAGE capture mode.");
        }

        if (callback == null) {
            throw new IllegalArgumentException("OnVideoSavedCallback should not be empty");
        }

        mVideoIsRecording.set(true);
        mVideoCapture.startRecording(
                file,
                executor,
                new VideoCapture.OnVideoSavedCallback() {
                    @Override
                    public void onVideoSaved(@NonNull File savedFile) {
                        mVideoIsRecording.set(false);
                        callback.onVideoSaved(savedFile);
                    }

                    @Override
                    public void onError(
                            @VideoCapture.VideoCaptureError int videoCaptureError,
                            @NonNull String message,
                            @Nullable Throwable cause) {
                        mVideoIsRecording.set(false);
                        Log.e(TAG, message, cause);
                        callback.onError(videoCaptureError, message, cause);
                    }
                });
    }

    public void stopRecording() {
        if (mVideoCapture == null) {
            return;
        }

        mVideoCapture.stopRecording();
    }

    public boolean isRecording() {
        return mVideoIsRecording.get();
    }

    // TODO(b/124269166): Rethink how we can handle permissions here.
    @SuppressLint("MissingPermission")
    public void setCameraLensFacing(@Nullable Integer lensFacing) {
        // Setting same lens facing is a no-op, so check for that first
        if (!Objects.equals(mCameraLensFacing, lensFacing)) {
            // If we're not bound to a lifecycle, just update the camera that will be opened when we
            // attach to a lifecycle.
            mCameraLensFacing = lensFacing;

            if (mCurrentLifecycle != null) {
                // Re-bind to lifecycle with new camera
                bindToLifecycle(mCurrentLifecycle);
            }
        }
    }

    @RequiresPermission(permission.CAMERA)
    public boolean hasCameraWithLensFacing(@CameraSelector.LensFacing int lensFacing) {
        String cameraId;
        try {
            cameraId = CameraX.getCameraWithLensFacing(lensFacing);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to query lens facing.", e);
        }

        return cameraId != null;
    }

    @Nullable
    public Integer getLensFacing() {
        return mCameraLensFacing;
    }

    public void toggleCamera() {
        // TODO(b/124269166): Rethink how we can handle permissions here.
        @SuppressLint("MissingPermission")
        Set<Integer> availableCameraLensFacing = getAvailableCameraLensFacing();

        if (availableCameraLensFacing.isEmpty()) {
            return;
        }

        if (mCameraLensFacing == null) {
            setCameraLensFacing(availableCameraLensFacing.iterator().next());
            return;
        }

        if (mCameraLensFacing == CameraSelector.LENS_FACING_BACK
                && availableCameraLensFacing.contains(CameraSelector.LENS_FACING_FRONT)) {
            setCameraLensFacing(CameraSelector.LENS_FACING_FRONT);
            return;
        }

        if (mCameraLensFacing == CameraSelector.LENS_FACING_FRONT
                && availableCameraLensFacing.contains(CameraSelector.LENS_FACING_BACK)) {
            setCameraLensFacing(CameraSelector.LENS_FACING_BACK);
            return;
        }
    }

    public float getZoomRatio() {
        if (mCamera != null) {
            return mCamera.getCameraInfo().getZoomState().getValue().getZoomRatio();
        } else {
            return UNITY_ZOOM_SCALE;
        }
    }

    public void setZoomRatio(float zoomRatio) {
        if (mCamera != null) {
            ListenableFuture<Void> future = mCamera.getCameraControl().setZoomRatio(
                    zoomRatio);
            Futures.addCallback(future, new FutureCallback<Void>() {
                @Override
                public void onSuccess(@Nullable Void result) {
                }

                @Override
                public void onFailure(Throwable t) {
                    // Throw the unexpected error.
                    throw new RuntimeException(t);
                }
            }, CameraXExecutors.directExecutor());
        } else {
            Log.e(TAG, "Failed to set zoom ratio");
        }
    }

    public float getMinZoomRatio() {
        if (mCamera != null) {
            return mCamera.getCameraInfo().getZoomState().getValue().getMinZoomRatio();
        } else {
            return UNITY_ZOOM_SCALE;
        }
    }

    public float getMaxZoomRatio() {
        if (mCamera != null) {
            return mCamera.getCameraInfo().getZoomState().getValue().getMaxZoomRatio();
        } else {
            return ZOOM_NOT_SUPPORTED;
        }
    }

    public boolean isZoomSupported() {
        return getMaxZoomRatio() != ZOOM_NOT_SUPPORTED;
    }

    // TODO(b/124269166): Rethink how we can handle permissions here.
    @SuppressLint("MissingPermission")
    private void rebindToLifecycle() {
        if (mCurrentLifecycle != null) {
            bindToLifecycle(mCurrentLifecycle);
        }
    }

    int getRelativeCameraOrientation(boolean compensateForMirroring) {
        int rotationDegrees = 0;
        if (mCamera != null) {
            rotationDegrees =
                    mCamera.getCameraInfo().getSensorRotationDegrees(getDisplaySurfaceRotation());
            if (compensateForMirroring) {
                rotationDegrees = (360 - rotationDegrees) % 360;
            }
        }

        return rotationDegrees;
    }

    public void invalidateView() {
        updateViewInfo();
    }

    void clearCurrentLifecycle() {
        if (mCurrentLifecycle != null && mCameraProvider != null) {
            // Remove previous use cases
            List<UseCase> toUnbind = new ArrayList<>();
            if (mImageCapture != null && mCameraProvider.isBound(mImageCapture)) {
                toUnbind.add(mImageCapture);
            }
            if (mVideoCapture != null && mCameraProvider.isBound(mVideoCapture)) {
                toUnbind.add(mVideoCapture);
            }
            //TODO: [SOUP] START
            if (mImageAnalysis != null && mCameraProvider.isBound(mImageAnalysis)) {
                toUnbind.add(mImageAnalysis);
            }
            //TODO: [SOUP] END
            if (mPreview != null && mCameraProvider.isBound(mPreview)) {
                toUnbind.add(mPreview);
            }

            if (!toUnbind.isEmpty()) {
                mCameraProvider.unbind(toUnbind.toArray((new UseCase[0])));
            }
        }
        mCamera = null;
        mCurrentLifecycle = null;
    }

    // Update view related information used in use cases
    private void updateViewInfo() {
        if (mImageCapture != null) {
            mImageCapture.setTargetAspectRatioCustom(new Rational(getWidth(), getHeight()));
            mImageCapture.setTargetRotation(getDisplaySurfaceRotation());
        }

        if (mVideoCapture != null) {
            mVideoCapture.setTargetRotation(getDisplaySurfaceRotation());
        }

        //TODO: [SOUP] START
        if (mImageAnalysis != null) {
            mImageAnalysis.setAnalyzer(CameraXExecutors.highPriorityExecutor(), mImageAnalysisAnalyzer);
            mImageAnalysis.setTargetRotation(getDisplaySurfaceRotation());
        }
        //TODO: [SOUP] END
    }

    @RequiresPermission(permission.CAMERA)
    private Set<Integer> getAvailableCameraLensFacing() {
        // Start with all camera directions
        Set<Integer> available = new LinkedHashSet<>(Arrays.asList(LensFacingConverter.values()));

        // If we're bound to a lifecycle, remove unavailable cameras
        if (mCurrentLifecycle != null) {
            if (!hasCameraWithLensFacing(CameraSelector.LENS_FACING_BACK)) {
                available.remove(CameraSelector.LENS_FACING_BACK);
            }

            if (!hasCameraWithLensFacing(CameraSelector.LENS_FACING_FRONT)) {
                available.remove(CameraSelector.LENS_FACING_FRONT);
            }
        }

        return available;
    }

    @ImageCapture.FlashMode
    public int getFlash() {
        return mFlash;
    }

    public void setFlash(@ImageCapture.FlashMode int flash) {
        this.mFlash = flash;

        if (mImageCapture == null) {
            // Do nothing if there is no imageCapture
            return;
        }

        mImageCapture.setFlashMode(flash);
    }

    public void enableTorch(boolean torch) {
        if (mCamera == null) {
            return;
        }
        ListenableFuture<Void> future = mCamera.getCameraControl().enableTorch(torch);
        Futures.addCallback(future, new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void result) {
            }

            @Override
            public void onFailure(Throwable t) {
                // Throw the unexpected error.
                throw new RuntimeException(t);
            }
        }, CameraXExecutors.directExecutor());
    }

    public boolean isTorchOn() {
        if (mCamera == null) {
            return false;
        }
        return mCamera.getCameraInfo().getTorchState().getValue() == TorchState.ON;
    }

    public Context getContext() {
        return mCameraView.getContext();
    }

    public int getWidth() {
        return mCameraView.getWidth();
    }

    public int getHeight() {
        return mCameraView.getHeight();
    }

    public int getDisplayRotationDegrees() {
        return CameraOrientationUtil.surfaceRotationToDegrees(getDisplaySurfaceRotation());
    }

    protected int getDisplaySurfaceRotation() {
        return mCameraView.getDisplaySurfaceRotation();
    }

    private int getMeasuredWidth() {
        return mCameraView.getMeasuredWidth();
    }

    private int getMeasuredHeight() {
        return mCameraView.getMeasuredHeight();
    }

    @Nullable
    public Camera getCamera() {
        return mCamera;
    }

    @NonNull
    public CameraView.CaptureMode getCaptureMode() {
        return mCaptureMode;
    }

    public void setCaptureMode(@NonNull CameraView.CaptureMode captureMode) {
        this.mCaptureMode = captureMode;
        rebindToLifecycle();
    }

    public long getMaxVideoDuration() {
        return mMaxVideoDuration;
    }

    public void setMaxVideoDuration(long duration) {
        mMaxVideoDuration = duration;
    }

    public long getMaxVideoSize() {
        return mMaxVideoSize;
    }

    public void setMaxVideoSize(long size) {
        mMaxVideoSize = size;
    }

    public boolean isPaused() {
        return false;
    }

    //TODO: [SOUP] START
    public void setAnalyzer(Analyzer analyzer) {
        mImageAnalysisAnalyzer = analyzer;

        if (mImageAnalysis == null) {
            return;
        }

        if (getCaptureMode() == CaptureMode.VIDEO) {
            throw new IllegalStateException("Can not set analyzer under VIDEO capture mode.");
        }

        if (analyzer == null) {
            throw new IllegalArgumentException("Analyzer should not be empty");
        }

        mImageAnalysis.setAnalyzer(CameraXExecutors.highPriorityExecutor(), analyzer);
    }

    public void removeAnalyzer() {
        mImageAnalysisAnalyzer = null;

        if (mImageAnalysis == null) {
            return;
        }

        mImageAnalysis.clearAnalyzer();
    }
    //TODO: [SOUP] END
}
