package com.example.messanger.presentation.fragment.call

import androidx.lifecycle.viewModelScope
import com.example.messanger.core.enumeration.CallState
import com.example.messanger.presentation.fragment.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CallViewModel( private val initCallState: CallState) : BaseViewModel()  {
    companion object {
        private const val TIMER_TICK = 1000L
        private const val PATTERN = "HH:mm:ss"
    }

    private val _timer = MutableStateFlow<String>("00:00:00")
    val timer = _timer.asStateFlow()
    private val _callState = MutableStateFlow<CallState>(CallState.AUDIO)
    val callState = _callState.asStateFlow()

    init {
        createTimer()
    }

    fun changeState(){
        val currentState = callState.value
        _callState.value = if (currentState == CallState.AUDIO) CallState.VIDEO else CallState.AUDIO
    }

    private fun createTimer(){
        viewModelScope.launch(Dispatchers.Default) {

            var totalTime = 0L
            val dateFormat = SimpleDateFormat(PATTERN, Locale.getDefault())

            while(true){
                delay(TIMER_TICK)
                totalTime += TIMER_TICK
                _timer.value = dateFormat.format(Date(totalTime))
            }
        }
    }

}