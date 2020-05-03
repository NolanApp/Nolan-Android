package soup.nolan.ui.share

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import soup.nolan.R

sealed class ShareItemUiModel {
    abstract fun getIcon(context: Context): Drawable?
    abstract fun getLabel(context: Context): String

    object Instagram : ShareItemUiModel() {

        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_instagram)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_instagram)
        }
    }

    object InstagramStory : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_instagram_story)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_instagram_story)
        }
    }

    object Facebook : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_facebook)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_facebook)
        }
    }

    object FacebookStory : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_facebook_story)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_facebook_story)
        }
    }

    object Line : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_line)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_line)
        }
    }

    object Twitter : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_twitter)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_twitter)
        }
    }

    object KakaoTalk : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_kakaotalk)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_kakaotalk)
        }
    }

    object WhatsApp : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_whatsapp)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_whatsapp)
        }
    }

    object More : ShareItemUiModel() {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_logo_more)
        }

        override fun getLabel(context: Context): String {
            return context.getString(R.string.share_target_more)
        }
    }
}
