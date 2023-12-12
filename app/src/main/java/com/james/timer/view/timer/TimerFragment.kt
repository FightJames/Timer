package com.james.timer.view.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.james.timer.R
import com.james.timer.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerFragment : Fragment() {
    private lateinit var timerViewBinding: FragmentTimerBinding
    private val timerViewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        timerViewModel.fetchTimerData()
        timerViewBinding.createTimerBtn.setOnClickListener {
            this.findNavController().navigate(R.id.action_timerFragment_to_createTimerFragment)
        }
    }
}