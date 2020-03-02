/*
 * Copyright 2019 The Android Open Source Project
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

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import static androidx.camera.view.ScaleTypeTransform.getFillScaleWithBufferAspectRatio;
import static androidx.camera.view.ScaleTypeTransform.getOriginOfCenteredView;
import static androidx.camera.view.ScaleTypeTransform.getRotationDegrees;

/**
 * The {@link TextureView} implementation for {@link PreviewView}
 */
public class TextureViewImplementation implements PreviewView.Implementation {

    private static final String TAG = "TextureViewImpl";

    private FrameLayout mParent;
    TextureView mTextureView;
    SurfaceTexture mSurfaceTexture;
    private Size mResolution;
    ListenableFuture<Void> mSurfaceReleaseFuture;
    SurfaceRequest mSurfaceRequest;

    @Override
    public void init(@NonNull FrameLayout parent) {
        mParent = parent;
    }

    @NonNull
    @Override
    public Preview.SurfaceProvider getSurfaceProvider() {
        return (surfaceRequest) -> {
            mResolution = surfaceRequest.getResolution();
            initInternal();
            if (mSurfaceRequest != null) {
                mSurfaceRequest.setWillNotComplete();
            }

            mSurfaceRequest = surfaceRequest;
            surfaceRequest.addRequestCancellationListener(
                    ContextCompat.getMainExecutor(mTextureView.getContext()), () -> {
                        if (mSurfaceRequest != null && mSurfaceRequest == surfaceRequest) {
                            mSurfaceRequest = null;
                            mSurfaceReleaseFuture = null;
                        }
                    });

            tryToProvidePreviewSurface();
        };
    }

    @Override
    public void onDisplayChanged() {
        if (mParent == null || mTextureView == null || mResolution == null) {
            return;
        }

        correctPreviewForCenterCrop(mParent, mTextureView, mResolution);
    }

    private void initInternal() {
        mTextureView = new TextureView(mParent.getContext());
        mTextureView.setLayoutParams(
                new FrameLayout.LayoutParams(mResolution.getWidth(), mResolution.getHeight()));
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(final SurfaceTexture surfaceTexture,
                    final int width, final int height) {
                mSurfaceTexture = surfaceTexture;
                tryToProvidePreviewSurface();
            }

            @Override
            public void onSurfaceTextureSizeChanged(final SurfaceTexture surfaceTexture,
                    final int width, final int height) {
                Log.d(TAG, "onSurfaceTextureSizeChanged(width:" + width + ", height: " + height
                        + " )");
            }

            /**
             * If a surface has been provided to the camera (meaning
             * {@link TextureViewImplementation#mSurfaceRequest} is null), but the camera
             * is still using it (meaning {@link TextureViewImplementation#mSurfaceReleaseFuture} is
             * not null), a listener must be added to
             * {@link TextureViewImplementation#mSurfaceReleaseFuture} to ensure the surface
             * is properly released after the camera is done using it.
             *
             * @param surfaceTexture The {@link SurfaceTexture} about to be destroyed.
             * @return false if the camera is not done with the surface, true otherwise.
             */
            @Override
            public boolean onSurfaceTextureDestroyed(final SurfaceTexture surfaceTexture) {
                mSurfaceTexture = null;
                if (mSurfaceRequest == null && mSurfaceReleaseFuture != null) {
                    Futures.addCallback(mSurfaceReleaseFuture, new FutureCallback<Void>() {
                        @Override
                        public void onSuccess(@Nullable Void result) {
                            surfaceTexture.release();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            if (t instanceof SurfaceRequest.RequestCancelledException) {
                                surfaceTexture.release();
                            } else {
                                throw new IllegalStateException("SurfaceReleaseFuture did not "
                                        + "complete nicely.", t);
                            }
                        }
                    }, ContextCompat.getMainExecutor(mTextureView.getContext()));
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void onSurfaceTextureUpdated(final SurfaceTexture surfaceTexture) {
            }
        });

        // Even though PreviewView calls `removeAllViews()` before calling init(), it should be
        // called again here in case `getPreviewSurfaceProvider()` is called more than once on
        // the same TextureViewImplementation instance.
        mParent.removeAllViews();
        mParent.addView(mTextureView);
    }

    @SuppressWarnings("WeakerAccess")
    void tryToProvidePreviewSurface() {
        /*
          Should only continue if:
          - The preview size has been specified.
          - The textureView's surfaceTexture is available (after TextureView
          .SurfaceTextureListener#onSurfaceTextureAvailable is invoked)
          - The surfaceCompleter has been set (after CallbackToFutureAdapter
          .Resolver#attachCompleter is invoked).
         */
        if (mResolution == null || mSurfaceTexture == null || mSurfaceRequest == null) {
            return;
        }

        mSurfaceTexture.setDefaultBufferSize(mResolution.getWidth(), mResolution.getHeight());

        final Surface surface = new Surface(mSurfaceTexture);
        final ListenableFuture<Void> surfaceReleaseFuture = mSurfaceRequest.setSurface(surface);
        mSurfaceReleaseFuture = surfaceReleaseFuture;
        mSurfaceReleaseFuture.addListener(() -> {
            surface.release();
            if (mSurfaceReleaseFuture == surfaceReleaseFuture) {
                mSurfaceReleaseFuture = null;
            }
        }, ContextCompat.getMainExecutor(mTextureView.getContext()));

        mSurfaceRequest = null;

        correctPreviewForCenterCrop(mParent, mTextureView, mResolution);
    }

    /**
     * Corrects the preview to match the UI orientation and completely fill the PreviewView.
     *
     * <p>
     * The camera produces a preview that depends on its sensor orientation and that has a
     * specific resolution. In order to display it correctly, this preview must be rotated to
     * match the UI orientation, and must be scaled up/down to fit inside the view that's
     * displaying it. This method takes care of doing so while keeping the preview centered.
     * </p>
     *
     * @param container   The {@link PreviewView}'s root layout, which wraps the preview.
     * @param textureView The {@link TextureView} that displays the preview, its size
     *                    must match the camera sensor output size.
     * @param bufferSize  The camera sensor output size.
     */
    private void correctPreviewForCenterCrop(@NonNull final View container,
            @NonNull final TextureView textureView, @NonNull final Size bufferSize) {
        // Scale TextureView to fill PreviewView while respecting sensor output size aspect ratio
        final Pair<Float, Float> scale = getFillScaleWithBufferAspectRatio(container, textureView,
                bufferSize);
        textureView.setScaleX(scale.first);
        textureView.setScaleY(scale.second);

        // Center TextureView inside PreviewView
        final Point newOrigin = getOriginOfCenteredView(container, textureView);
        textureView.setX(newOrigin.x);
        textureView.setY(newOrigin.y);

        // Rotate TextureView to correct preview orientation
        final int rotation = getRotationDegrees(textureView);
        textureView.setRotation(-rotation);
    }
}
