package soup.nolan.model

import android.content.Context
import android.content.pm.PackageManager

sealed class ThirdPartyApp(val packageName: String) {
    object Instagram : ThirdPartyApp("com.instagram.android")
    object Facebook : ThirdPartyApp("com.facebook.katana")
    object Twitter : ThirdPartyApp("com.twitter.android")
    object Line : ThirdPartyApp("jp.naver.line.android")
    object KakaoTalk : ThirdPartyApp("com.kakao.talk")
    object WhatsApp : ThirdPartyApp("com.whatsapp")

    fun isInstalled(context: Context): Boolean {
        return context.packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
            .any { it.packageName == packageName }
    }

    companion object {

        fun list(): List<ThirdPartyApp> {
            return listOf(Instagram, Facebook, Twitter, Line, KakaoTalk, WhatsApp)
        }
    }
}
