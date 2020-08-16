package soup.nolan.ui.filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import soup.nolan.R
import soup.nolan.databinding.FilterEditBinding
import soup.nolan.ui.filter.FilterEditFragmentDirections.Companion.actionToCamera
import soup.nolan.ui.filter.FilterEditListAdapter.Companion.VIEW_TYPE_HEADER
import soup.nolan.ui.utils.setOnDebounceClickListener

class FilterEditFragment : Fragment(R.layout.filter_edit) {

    private val viewModel: FilterEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FilterEditBinding.bind(view).apply {
            val adapter = FilterEditListAdapter {
                when (it) {
                    is FilterEditUiModel.Header -> {
                        //TODO: show dialog
                    }
                    is FilterEditUiModel.Item -> viewModel.onItemClick(it)
                }
            }
            listView.layoutManager = GridLayoutManager(view.context, 4).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter.getItemViewType(position)) {
                            VIEW_TYPE_HEADER -> 4
                            else -> 1
                        }
                    }
                }
            }
            listView.itemAnimator = null
            listView.adapter = adapter
            viewModel.uiModel.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })

            startButton.setOnDebounceClickListener {
                findNavController().navigate(actionToCamera())
            }
            viewModel.canStart.observe(viewLifecycleOwner, Observer {
                startButton.isEnabled = it
            })
        }
    }
}
