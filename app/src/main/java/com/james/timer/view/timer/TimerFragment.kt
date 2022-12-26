package com.james.timer.view.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.james.timer.R
import com.james.timer.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTimerBinding.inflate(inflater, container, false).let {
        timerViewBinding = it
        it.root
    }

    override fun onStart() {
        super.onStart()
        adapter.viewLifeCycleScope = this.viewLifecycleOwner.lifecycleScope

        timerViewModel.fetchTimerData()
        timerViewBinding.createTimerBtn.setOnClickListener {
            this.findNavController().navigate(R.id.action_timerFragment_to_createTimerFragment)
        }
        timerViewBinding.timerRecyclerView.layoutManager =
            LinearLayoutManager(this.requireContext())
        timerViewBinding.timerRecyclerView.adapter = adapter
        timerViewModel.timerDataListLiveData.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }
}