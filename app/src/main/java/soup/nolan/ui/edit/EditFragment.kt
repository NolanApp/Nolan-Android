package soup.nolan.ui.edit

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toFile
import androidx.navigation.fragment.navArgs
import soup.nolan.R
import soup.nolan.databinding.EditFragmentBinding
import soup.nolan.temp.StyleTransfer
import soup.nolan.ui.base.BaseFragment
import timber.log.Timber

class EditFragment : BaseFragment() {

    private val args: EditFragmentArgs by navArgs()
    private val viewModel: EditViewModel by viewModel()

    private lateinit var binding: EditFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initViewState(binding)
        return binding.root
    }

    private fun initViewState(binding: EditFragmentBinding) {
        binding.editImageView.setImageURI(args.fileUri)
        val style = BitmapFactory.decodeResource(resources, R.drawable.style)
        val input = BitmapFactory.decodeFile(args.fileUri.toFile().path)
        val start = System.currentTimeMillis()
        StyleTransfer.transform(style, input)
            .addOnSuccessListener {
                Timber.d("success: $it ${System.currentTimeMillis() - start}ms")
                activity?.runOnUiThread {
                    binding.editImageView.setImageBitmap(it)
                }
            }
            .addOnFailureListener {
                Timber.d("failure: $it")
            }
    }
}
