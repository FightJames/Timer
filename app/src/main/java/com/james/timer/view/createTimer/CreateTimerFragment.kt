package com.james.timer.view.createTimer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.viewModels
import com.james.timer.R
import com.james.timer.databinding.FragmentCreateTimerBinding

class CreateTimerFragment : Fragment() {
    lateinit var binding: FragmentCreateTimerBinding
    val createTimerViewModel: CreateTimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.numberGridLayout.let { gridLayout ->
            var currentRow = 0
            for (i in 1..9) {
                val view = createKeyboardNumberItem(
                    this.layoutInflater,
                    currentRow,
                    (i - 1) % 3,
                    R.layout.number_item,
                    gridLayout
                ) as TextView
                view.text = i.toString()
                gridLayout.addView(view)
                view.setOnClickListener {
                    createTimerViewModel.appendTime(i.toString())
                }
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
            ) as TextView
            view.text = "0"
            gridLayout.addView(view)
            view = createKeyboardNumberItem(
                this.layoutInflater,
                currentRow,
                1,
                R.layout.number_item,
                gridLayout
            ) as TextView
            view.text = "00"
            gridLayout.addView(view)
            view = createKeyboardNumberItem(
                this.layoutInflater,
                currentRow,
                0,
                R.layout.back_item,
                gridLayout
            ) as TextView
            gridLayout.addView(view)
        }
    }

    fun createKeyboardNumberItem(
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
        createTimerViewModel.currentTimerTimeLiveData.observe(this.viewLifecycleOwner) {
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCreateTimerBinding.inflate(inflater, container, false).let {
        binding = it
        it.root
    }

    companion object {
        fun newInstance() = CreateTimerFragment()
    }
}