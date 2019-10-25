package soup.nolan.ui.edit

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toFile
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import soup.nolan.R
import soup.nolan.databinding.EditFragmentBinding
import soup.nolan.stylize.experimental.PopStyleTransfer
import soup.nolan.stylize.experimental.StyleTransfer
import soup.nolan.ui.base.BaseFragment
import soup.nolan.ui.edit.EditFragmentDirections.Companion.actionToShare
import soup.nolan.ui.utils.setOnDebounceClickListener
import timber.log.Timber

class EditFragment : BaseFragment() {

    private val args: EditFragmentArgs by navArgs()
    private val viewModel: EditViewModel by viewModel()

    private val transfer by lazy { PopStyleTransfer() }

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
        binding.shareButton.setOnDebounceClickListener {
            findNavController().navigate(actionToShare(args.fileUri))
        }
        val style = BitmapFactory.decodeResource(resources, R.drawable.style)
        val input = FirebaseVisionImage.fromFilePath(requireContext(), args.fileUri).bitmap
        binding.editImageView.setImageBitmap(input)
        val start = System.currentTimeMillis()
        transfer.transform(input)
            .addOnSuccessListener {
                val duration = System.currentTimeMillis() - start
                Timber.d("success: $it $duration ms")
                activity?.runOnUiThread {
                    binding.editImageView.setImageBitmap(it)
                }
                Toast.makeText(context, "Success! ($duration ms)", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Timber.d("failure: $it")
                Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
            }
    }
}
