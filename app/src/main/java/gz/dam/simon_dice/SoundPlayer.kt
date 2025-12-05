package gz.dam.simon_dice

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

/**
 * Clase para gestionar la reproducción de sonidos
 * Implementa el patrón Singleton para evitar múltiples instancias
 */
class SoundPlayer private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: SoundPlayer? = null

        fun getInstance(context: Context): SoundPlayer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundPlayer(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var sonidoActual: Int = -1

    /**
     * Reproduce un sonido
     */
    fun playSound(soundResource: Int) {
        try {
            // Detener sonido anterior si está reproduciéndose
            stopSound()

            mediaPlayer = MediaPlayer.create(context, soundResource).apply {
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                }
                start()
            }
            sonidoActual = soundResource

        } catch (e: Exception) {
            Log.e("SoundPlayer", "Error reproduciendo sonido: ${e.message}")
        }
    }

    /**
     * Reproduce sonido de un color
     */
    fun playColorSound(colorInt: Int) {
        val soundResource = when (colorInt) {
            0 -> R.raw.rojo
            1 -> R.raw.verde
            2 -> R.raw.azul
            3 -> R.raw.amarillo
            else -> R.raw.error
        }
        playSound(soundResource)
    }

    /**
     * Reproduce sonido de error
     */
    fun playErrorSound() {
        playSound(R.raw.error)
    }

    /**
     * Reproduce sonido de victoria
     */
    fun playVictorySound() {
        playSound(R.raw.victoria)
    }

    /**
     * Detiene el sonido actual
     */
    fun stopSound() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
            mediaPlayer = null
        }
        sonidoActual = -1
    }

    /**
     * Libera recursos
     */
    fun release() {
        stopSound()
        INSTANCE = null
    }
}