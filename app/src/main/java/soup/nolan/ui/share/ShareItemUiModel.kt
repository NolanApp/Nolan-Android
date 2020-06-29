package soup.nolan.ui.share

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import soup.nolan.R

sealed class ShareItemUiModel {
    abstract fun getIcon(context: Context): Drawable?
    abstract fun getLabel(context: Context): String
    abstract fun share(activity: Activity, shareImageUri: Uri)

    object Instagram : ShareItemUiModel() {

        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_instagram)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_instagram)
        }

        override fun share(activity: Activity, shareImageUri: Uri) {
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, shareImageUri)
                type = "image/jpeg"
                setPackage("com.instagram.android")
            }
            activity.startActivity(Intent.createChooser(intent, activity.getText(R.string.share)))
        }
    }

    object Facebook : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_facebook)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_facebook)
        }

        override fun share(activity: Activity, shareImageUri: Uri) {
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, shareImageUri)
                type = "image/jpeg"
                setPackage("com.facebook.katana")
            }
            activity.startActivity(Intent.createChooser(intent, activity.getText(R.string.share)))
        }
    }

    object Line : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_line)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_line)
        }

        override fun share(activity: Activity, shareImageUri: Uri) {
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, shareImageUri)
                type = "image/jpeg"
                setPackage("jp.naver.line.android")
            }
            activity.startActivity(Intent.createChooser(intent, activity.getText(R.string.share)))
        }
    }

    object Twitter : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_twitter)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_twitter)
        }

        override fun share(activity: Activity, shareImageUri: Uri) {
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, shareImageUri)
                type = "image/jpeg"
                setPackage("com.twitter.android")
            }
            activity.startActivity(Intent.createChooser(intent, activity.getText(R.string.share)))
        }
    }

    object KakaoTalk : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_kakaotalk)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_kakaotalk)
        }

        override fun share(activity: Activity, shareImageUri: Uri) {
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, shareImageUri)
                type = "image/jpeg"
                setPackage("com.kakao.talk")
            }
            activity.startActivity(Intent.createChooser(intent, activity.getText(R.string.share)))
        }
    }

    object WhatsApp : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_whatsapp)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_whatsapp)
        }

        override fun share(activity: Activity, shareImageUri: Uri) {
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, shareImageUri)
                type = "image/jpeg"
                setPackage("com.whatsapp")
            }
            activity.startActivity(Intent.createChooser(intent, activity.getText(R.string.share)))
        }
    }

    object More : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_more)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_more)
        }

        override fun share(activity: Activity, shareImageUri: Uri) {
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, shareImageUri)
                type = "image/jpeg"
            }
            activity.startActivity(Intent.createChooser(intent, activity.getText(R.string.share)))
        }
    }
}
