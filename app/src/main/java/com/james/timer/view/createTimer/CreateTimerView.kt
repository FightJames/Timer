package com.james.timer.view.createTimer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.james.timer.R
import com.james.timer.model.Time
import com.james.timer.utils.StringFormatter
import com.james.timer.view.createTimer.CreateTimerViewModel.Companion.ZERO
import kotlinx.coroutines.launch

@Composable
fun CreateTimerView(viewModel: CreateTimerViewModel = viewModel(), navController: NavController) {
    val time: Time by viewModel.currentTimerTimeStateFlow.collectAsStateWithLifecycle()
    Box {
        Column {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(0.dp, 25.dp, 0.dp, 0.dp)
                    .wrapContentSize(),
                text = StringFormatter.timerString(time, LocalContext.current),
                fontSize = 50.sp
            )
            Spacer(modifier = Modifier.height(50.dp))
            numberKeyboard(viewModel)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(30.dp, 0.dp, 0.dp, 50.dp),
        ) {
            Image(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        navController.navigateUp()
                    },
                painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                contentDescription = "",
                colorFilter = ColorFilter.tint(color = Color.Blue),
            )
            if (time.hours != ZERO || time.minutes != ZERO || time.seconds != ZERO) {
                val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
                Spacer(modifier = Modifier.width(60.dp))
                Image(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .align(Alignment.CenterVertically)
                        .padding(0.dp, 0.dp, 0.dp, 0.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false),
                        ) {
                            lifecycleScope.launch {
                                viewModel.currentTimerTimeStateFlow.value.let {
                                    viewModel.addAndStartTimer(it)
                                    navController.navigateUp()
                                }
                            }
                        },
                    painter = painterResource(id = R.drawable.ic_baseline_play_circle_24),
                    contentDescription = "",
                )
            }
        }
    }
}


@Composable
fun numberKeyboard(viewModel: CreateTimerViewModel = viewModel()) {
    val list = mutableListOf<String>()
    for (i in 1..9) {
        list.add(i.toString())
    }
    list.add("0")
    list.add("00")
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(list) {
                numberItem(number = it) {
                    viewModel.appendTime(it)
                }
            }
            item {
                BackItem {
                    viewModel.deleteLastElementInCurrentCreateTime()
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun BackItem(onClick: () -> Unit = {}) {
    Box(
        Modifier
            .width(100.dp)
            .height(100.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = {
                    onClick()
                }
            )) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.ic_baseline_circle_24),
            contentDescription = ""
        )

        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .width(50.dp)
                .height(50.dp)
                .padding(0.dp, 0.dp, 6.dp, 0.dp),
            painter = painterResource(id = R.drawable.ic_baseline_backspace_24),
            contentDescription = "",
        )
    }
}

@Composable
fun numberItem(
    number: String,
    onClick: (String) -> Unit = {}
) {
    Box(
        Modifier
            .width(100.dp)
            .height(100.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onClick = {
                    onClick.invoke(number)
                }
            )) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.ic_baseline_circle_24),
            contentDescription = ""
        )
        Text(
            text = number,
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize(),
            fontSize = 50.sp
        )
    }
}
