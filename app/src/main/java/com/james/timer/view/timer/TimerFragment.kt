package com.james.timer.view.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.james.timer.R
import com.james.timer.databinding.FragmentTimerBinding
import com.james.timer.model.TimerData
import com.james.timer.ui.theme.TimerUITheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerFragment : Fragment() {
    private lateinit var timerViewBinding: FragmentTimerBinding
    private val timerViewModel: TimerViewModel by viewModels()
    private lateinit var adapter: TimerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TimerAdapter(
            timerViewModel.timerDataListLiveData.value ?: emptyList(),
            timerViewModel
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                timerViewModel.timerUIStateFlow.collect {
                    timerViewBinding.timerComposeView.setContent {
                        TimerView(it)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTimerBinding.inflate(inflater, container, false).let {
        timerViewBinding = it
        timerViewBinding.timerComposeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        it.root
    }

    override fun onStart() {
        super.onStart()
//        adapter.viewLifeCycleScope = this.viewLifecycleOwner.lifecycleScope
//
        timerViewModel.fetchTimerData()
        timerViewBinding.createTimerBtn.setOnClickListener {
            this.findNavController().navigate(R.id.action_timerFragment_to_createTimerFragment)
        }
//        timerViewBinding.timerRecyclerView.layoutManager =
//            LinearLayoutManager(this.requireContext())
//        timerViewBinding.timerRecyclerView.adapter = adapter
//        timerViewModel.timerDataListLiveData.observe(viewLifecycleOwner) {
//            adapter.updateData(it) {
//                timerViewBinding.timerRecyclerView.scrollToPosition(0)
//            }
//        }
    }
}