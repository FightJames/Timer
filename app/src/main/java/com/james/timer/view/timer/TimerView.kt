package com.james.timer.view.timer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.james.timer.R
import com.james.timer.model.TimerData
import com.james.timer.model.TimerState
import com.james.timer.ui.theme.TimerUITheme
import com.james.timer.utils.rethrowOnCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import milliSecondToTimeString
import timber.log.Timber


@Composable
fun TimerView(state: TimerViewModel.TimerUIState) {
    TimerUITheme {
        when (state) {
            is TimerViewModel.TimerUIState.Idle -> {
                //loading
            }

            is TimerViewModel.TimerUIState.UpdateTimerList -> {
                Log.d("james-test", "pass list to compose ${state.list}")
                TimerListView(state.list)
            }
        }
    }
}

@Composable
fun TimerListView(data: List<TimerData>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(data) { item ->
            TimerItemView(item)
        }
    }
}

@Composable
fun TimerItemView(
    data: TimerData,
    viewModel: TimerViewModel = viewModel()
) {
    var countDownTimer by remember { mutableStateOf<String>("") }
    LaunchedEffect(key1 = data.createTime) {
        try {
            countDownTimer = milliSecondToTimeString(viewModel.getTimerCurrentTime(data.createTime))
            viewModel.getTimerCurrentTimeFlow(data.createTime).collect {
                withContext(Dispatchers.Main.immediate) {
                    countDownTimer = milliSecondToTimeString(it)
                }
            }
        } catch (t: Throwable) {
            t.rethrowOnCancellation()
            Timber.e("Exception $t")
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp, 0.dp, 0.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp, 0.dp, 0.dp, 0.dp)
                            .align(Alignment.TopStart),
                        text = "${milliSecondToTimeString(data.countDownTime)} ${stringResource(id = R.string.timer)}"
                    )
                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(0.dp, 0.dp, 8.dp, 0.dp)
                            .width(24.dp)
                            .height(24.dp)
                            .clickable {
                                viewModel.removeTimer(data)
                            },
                        painter = painterResource(id = R.drawable.ic_baseline_cancel_24),
                        contentDescription = ""
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp, 0.dp, 8.dp)
                ) {
                    Column(
                        Modifier
                            .wrapContentSize()
                            .padding(8.dp, 0.dp, 0.dp, 0.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            modifier = Modifier.wrapContentWidth(),
                            fontSize = 50.sp,
                            text = countDownTimer,
                        )
                        if (data.state != TimerState.STOP) {
                            Image(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                                    .width(24.dp)
                                    .height(24.dp)
                                    .clickable(enabled = true) {
                                        viewModel.stop(data.createTime)
                                    },
                                painter = painterResource(id = R.drawable.ic_baseline_replay_24),
                                contentDescription = ""
                            )
                        }
                    }
                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(0.dp, 0.dp, 8.dp, 0.dp)
                            .width(50.dp)
                            .height(50.dp)
                            .clickable(enabled = true) {
                                if (data.state == TimerState.RUNNING) {
                                    viewModel.pause(data.createTime)
                                } else {
                                    viewModel.start(data.createTime)
                                }
                            },
                        painter = painterResource(
                            id =
                            if (data.state == TimerState.RUNNING) {
                                R.drawable.ic_baseline_retengle_pause_24
                            } else {
                                R.drawable.ic_baseline_retengle_play_24
                            }
                        ),
                        contentDescription = "",
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}
