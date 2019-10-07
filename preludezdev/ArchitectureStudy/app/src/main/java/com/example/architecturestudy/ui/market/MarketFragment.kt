package com.example.architecturestudy.ui.market

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.architecturestudy.R
import com.example.architecturestudy.base.BaseFragment
import com.example.architecturestudy.data.source.CoinsRepositoryImpl
import com.example.architecturestudy.data.source.local.CoinsLocalDataSource
import com.example.architecturestudy.data.source.remote.CoinsRemoteDataSource
import com.example.architecturestudy.databinding.FragmentMarketBinding
import com.example.architecturestudy.network.RetrofitHelper
import com.example.architecturestudy.viewmodel.MarketViewModel

class MarketFragment : BaseFragment<FragmentMarketBinding>(R.layout.fragment_market) {

    private val marketViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MarketViewModel(
                    CoinsRepositoryImpl.getInstance(
                        CoinsRemoteDataSource.getInstance(RetrofitHelper.getInstance().coinApiService),
                        CoinsLocalDataSource.getInstance()
                    )
                ) as T
            }
        }).get(MarketViewModel::class.java)
    }

    private val rvAdapter = RecyclerViewAdapter()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModel()
        initRecyclerView()
        initCallback()

        marketViewModel.loadData(arguments?.getString(KEY_MARKET))
    }

    private fun initViewModel() {
        binding.viewModel = marketViewModel // 뷰모델을 레이아웃과 바인딩
    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = rvAdapter
    }

    private fun initCallback() {
        marketViewModel.notificationMsg.observe(this, Observer<String> { msg ->
            if (!msg.isNullOrEmpty()) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
        })
    }

    companion object {
        private const val KEY_MARKET = "KEY_MARKET"

        fun newInstance(market: String) = MarketFragment().apply {
            arguments = Bundle().apply { putString(KEY_MARKET, market) }
        }
    }
}
