package com.apt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apt.app.data.AptStore
import com.apt.app.data.AptViewModel
import com.apt.app.ui.link.LinkScreen
import com.apt.app.ui.main.MainScreen
import com.apt.app.ui.theme.AptTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AptTheme {
                val vm: AptViewModel = viewModel { AptViewModel(AptStore(applicationContext)) }
                Crossfade(targetState = vm.profile.linked, label = "inicio") { linked ->
                    if (linked) MainScreen(vm) else LinkScreen(vm)
                }
            }
        }
    }
}
