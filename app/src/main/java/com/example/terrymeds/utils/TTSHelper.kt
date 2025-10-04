package com.example.terrymeds.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.*
import com.example.terrymeds.data.MedicamentoUsuario
import java.util.*

/**
 * Helper class para manejar Text-to-Speech (TTS)
 */
class TTSHelper(private val context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized by mutableStateOf(false)
    private var _isPlaying by mutableStateOf(false)
    
    // Propiedad pública para acceder al estado
    val isPlaying: Boolean get() = _isPlaying

    init {
        initializeTTS()
    }

    private fun initializeTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("es", "ES"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Si español no está disponible, usar inglés
                    textToSpeech?.setLanguage(Locale.US)
                }
                isInitialized = true

                // Configurar listener para saber cuando termina de hablar
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isPlaying = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isPlaying = false
                    }

                    override fun onError(utteranceId: String?) {
                        _isPlaying = false
                    }
                })
            }
        }
    }

    fun speak(text: String) {
        if (isInitialized && textToSpeech != null) {
            if (_isPlaying) {
                stop()
            } else {
                textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "medicamento_info")
            }
        }
    }

    fun stop() {
        textToSpeech?.stop()
        _isPlaying = false
    }

    fun release() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
        _isPlaying = false
    }
}

/**
 * Composable para manejar TTS en Compose
 */
@Composable
fun rememberTTSHelper(context: Context): TTSHelper {
    val ttsHelper = remember { TTSHelper(context) }

    DisposableEffect(ttsHelper) {
        onDispose {
            ttsHelper.release()
        }
    }

    return ttsHelper
}

/**
 * Genera el texto para leer un medicamento
 */
fun generateMedicamentoSpeechText(medicamento: MedicamentoUsuario): String {
    val texto = buildString {
        append("Medicamento: ${medicamento.nombreMedicamento}. ")

        medicamento.concentracion?.let { concentracion ->
            append("Concentración: $concentracion. ")
        }

        append("Forma farmacéutica: ${medicamento.formaFarmaceutica.name.lowercase()}. ")
        append("Dosis: ${medicamento.cantidadPorDosis} ${medicamento.unidadDosis.name.lowercase()}. ")
        append("Frecuencia: cada ${medicamento.intervaloEntreDosisHoras} horas. ")

        medicamento.instruccionesAdicionales?.let { instrucciones ->
            append("Instrucciones adicionales: $instrucciones. ")
        }

        if (medicamento.numeroTotalDosis != null) {
            append("Tratamiento con duración limitada. ")
        } else {
            append("Tratamiento continuo. ")
        }
    }

    return texto
}
