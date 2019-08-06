package soup.nolan.ui

import android.os.Bundle
import soup.nolan.R
import soup.nolan.ui.base.BaseActivity

class NolanActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Nolan_Main)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nolan_activity)
    }
}
