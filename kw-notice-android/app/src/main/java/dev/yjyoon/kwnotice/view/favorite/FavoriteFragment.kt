package dev.yjyoon.kwnotice.view.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.yjyoon.kwnotice.KWNoticeApplication
import dev.yjyoon.kwnotice.data.db.entity.FavNotice
import dev.yjyoon.kwnotice.data.remote.model.KwHomeNotice
import dev.yjyoon.kwnotice.databinding.FragmentFavoriteBinding
import dev.yjyoon.kwnotice.view.notice.webview.WebViewActivity
import dev.yjyoon.kwnotice.viewmodel.favorite.FavNoticeViewModel
import dev.yjyoon.kwnotice.viewmodel.favorite.FavNoticeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private val binding by lazy { FragmentFavoriteBinding.inflate(layoutInflater) }

    private lateinit var recyclerView: RecyclerView

    private val viewModel: FavNoticeViewModel by activityViewModels {
        FavNoticeViewModelFactory(
            (activity?.application as KWNoticeApplication).favNoticeDatabase.favNoticeDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val showNotice: (FavNotice) -> Unit = { notice: FavNotice ->
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra("url", notice.url)
            startActivity(intent)
        }

        val deleteFav: (Long, String) -> Unit = { noticeId: Long, type: String ->
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.deleteFavNotice(noticeId, type)
            }
        }

        recyclerView = binding.favNoticeList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val favNoviceListAdapter = FavNoticeListAdapter(
            showNotice,
            deleteFav
        )

        lifecycle.coroutineScope.launch {
            viewModel.favNotices().collect() {
                favNoviceListAdapter.submitList(it)
                binding.emptyList.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            }
        }

        recyclerView.adapter = favNoviceListAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}