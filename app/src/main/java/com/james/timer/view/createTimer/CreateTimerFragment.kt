package com.james.timer.view.createTimer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import clickWithDebounce
import com.james.timer.R
import com.james.timer.databinding.FragmentCreateTimerBinding
import com.james.timer.ui.theme.TimerUITheme
import com.james.timer.utils.StringFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateTimerFragment : Fragment() {
    lateinit var binding: FragmentCreateTimerBinding
    val createTimerViewModel: CreateTimerViewModel by viewModels()

    private fun createKeyboardNumberItem(
        inflater: LayoutInflater,
        row: Int,
        column: Int,
        @LayoutRes layoutId: Int,
        parent: ViewGroup
    ): View {
        val view = inflater.inflate(layoutId, parent, false)
        view.layoutParams = GridLayout.LayoutParams(view.layoutParams).apply {
            rowSpec = GridLayout.spec(row)
            columnSpec = GridLayout.spec(column)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        setUpComposeView()
//        setUpView()
//        createTimerViewModel.currentTimerTimeLiveData.observe(this.viewLifecycleOwner) {
//            binding.timerText.text = StringFormatter.timerString(it, this.requireContext()).apply {
//                if (it.hours != CreateTimerViewModel.ZERO || it.minutes != CreateTimerViewModel.ZERO || it.seconds != CreateTimerViewModel.ZERO) {
//                    binding.startBtn.visibility = View.VISIBLE
//                } else {
//                    binding.startBtn.visibility = View.INVISIBLE
//                }
//            }
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCreateTimerBinding.inflate(inflater, container, false).let {
        binding = it
        binding.createTimerComposeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        it.root
    }

    private fun setUpComposeView() {
        binding.createTimerComposeView.setContent {
            TimerUITheme {
                Surface {
                    createTimerView(viewModel = createTimerViewModel, this.findNavController())
                }
            }
        }
    }

    private fun setUpView() {
        binding.numberGridLayout.let { gridLayout ->
            var currentRow = 0
            for (i in 1..9) {
                val view = createKeyboardNumberItem(
                    this.layoutInflater,
                    currentRow,
                    (i - 1) % 3,
                    R.layout.number_item,
                    gridLayout
                )
                view.findViewById<TextView>(R.id.numberText).let {
                    it.text = i.toString()
                    it.setOnClickListener {
                        createTimerViewModel.appendTime(i.toString())
                    }
                }
                gridLayout.addView(view)
                if (i % 3 == 0) {
                    currentRow++
                }
            }
            var view = createKeyboardNumberItem(
                this.layoutInflater,
                currentRow,
                0,
                R.layout.number_item,
                gridLayout
            )
            view.findViewById<TextView>(R.id.numberText).let {
                it.text = "00"
                it.setOnClickListener {
                    createTimerViewModel.appendTime("00")
                }
            }
            gridLayout.addView(view)
            view = createKeyboardNumberItem(
                this.layoutInflater,
                currentRow,
                1,
                R.layout.number_item,
                gridLayout
            )
            view.findViewById<TextView>(R.id.numberText).let {
                it.text = "0"
                it.setOnClickListener {
                    createTimerViewModel.appendTime("0")
                }
            }
            gridLayout.addView(view)
            createKeyboardNumberItem(
                this.layoutInflater,
                currentRow,
                2,
                R.layout.back_item,
                gridLayout
            ).let {
                gridLayout.addView(it)
                it.findViewById<ImageView>(R.id.backImage).setOnClickListener {
                    createTimerViewModel.deleteLastElementInCurrentCreateTime()
                }
            }
        }
        binding.deleteBtn.setOnClickListener {
            this.findNavController().navigateUp()
        }
        binding.startBtn.clickWithDebounce {
            this.lifecycleScope.launch {
                createTimerViewModel.currentTimerTimeLiveData.value?.let {
                    createTimerViewModel.addAndStartTimer(it)
                }
                this@CreateTimerFragment.findNavController().navigateUp()
            }
        }
    }
}