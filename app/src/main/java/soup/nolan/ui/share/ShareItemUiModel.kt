package soup.nolan.ui.share

class ShareItemUiModel(
    val target: ShareTarget,
    val packageName: String
)

enum class ShareTarget {
    INSTAGRAM,
    INSTAGRAM_STORY,
    FACEBOOK,
    FACEBOOK_STORY,
    LINE,
    TWITTER,
    KAKAOTALK,
    WHATSAPP,
    MORE
}
