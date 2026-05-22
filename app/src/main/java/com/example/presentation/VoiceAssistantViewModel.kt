package com.example.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.domain.CommandExecutor
import com.example.domain.VoiceAssistantManager

class VoiceAssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val commandExecutor = CommandExecutor(application)
    private val voiceAssistantManager = VoiceAssistantManager(application, commandExecutor)

    val isListening = voiceAssistantManager.isListening
    val recognizedText = voiceAssistantManager.recognizedText
    val statusText = voiceAssistantManager.statusText

    fun startListening() {
        voiceAssistantManager.startListening()
    }

    fun stopListening() {
        voiceAssistantManager.stopListening()
    }

    override fun onCleared() {
        super.onCleared()
        voiceAssistantManager.destroy()
    }
}
