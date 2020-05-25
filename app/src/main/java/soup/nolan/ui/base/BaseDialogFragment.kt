package soup.nolan.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatDialogFragment
import soup.nolan.R

abstract class BaseDialogFragment : AppCompatDialogFragment {

    @LayoutRes
    private var contentLayoutId = 0

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super() {
        this.contentLayoutId = contentLayoutId
    }

    override fun getTheme(): Int = R.style.Theme_Nolan_Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, theme)
    }

    @MainThread
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (contentLayoutId != 0) {
            return inflater.inflate(contentLayoutId, container, false)
        }
        return null
    }
}