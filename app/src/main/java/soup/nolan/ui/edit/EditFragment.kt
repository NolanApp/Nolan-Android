package soup.nolan.ui.edit

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import soup.nolan.BuildConfig
import soup.nolan.databinding.EditFragmentBinding
import soup.nolan.filter.stylize.LegacyStyleTransfer
import soup.nolan.stylize.popart.PopStyleTransfer
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.edit.EditFragmentDirections.Companion.actionToShare
import soup.nolan.ui.utils.setOnDebounceClickListener
import soup.nolan.ui.utils.toast
import timber.log.Timber

class EditFragment : BaseFragment() {

    private val args: EditFragmentArgs by navArgs()
    private val viewModel: EditViewModel by viewModel()

    private val transfer by lazy { PopStyleTransfer() }
    private val legacyTransfer by lazy { LegacyStyleTransfer(requireContext()) }

    private lateinit var binding: EditFragmentBinding

    private var lastBitmap: Bitmap? = null

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
        binding.loadingView.show()

        binding.saveButton.setOnDebounceClickListener {
            val bitmap = lastBitmap
            if (bitmap != null) {
                Gallery.saveBitmap(it.context, bitmap)
            }
        }
        binding.shareButton.setOnDebounceClickListener {
            findNavController().navigate(actionToShare(args.fileUri))
        }
        val input = FirebaseVisionImage.fromFilePath(requireContext(), args.fileUri).bitmap
        binding.editImageView.setImageBitmap(input)
        lastBitmap = input
        val start = System.currentTimeMillis()
        legacyTransfer.transform(input)
            .addOnSuccessListener {
                val duration = System.currentTimeMillis() - start
                Timber.d("success: $it $duration ms")
                lastBitmap = it
                activity?.runOnUiThread {
                    binding.editImageView.setImageBitmap(it)
                }
                if (BuildConfig.DEBUG) {
                    toast("Success! ($duration ms)")
                }
            }
            .addOnFailureListener {
                Timber.d("failure: $it")
                if (BuildConfig.DEBUG) {
                    toast("Error: $it")
                }
            }
            .addOnCompleteListener {
                binding.loadingView.hide()
            }
    }
}
