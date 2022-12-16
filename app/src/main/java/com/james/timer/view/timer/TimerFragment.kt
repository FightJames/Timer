package com.james.timer.view.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.james.timer.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {
    private lateinit var timerViewBinding: FragmentTimerBinding
    private val timerViewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    companion object {
        fun newInstance() = TimerFragment()
    }
}