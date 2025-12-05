package gz.dam.simon_dice



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import gz.dam.simon_dice.GameState
import gz.dam.simon_dice.ui.theme.SimonBlue
import gz.dam.simon_dice.ui.theme.SimonGreen
import gz.dam.simon_dice.ui.theme.SimonRed
import gz.dam.simon_dice.ui.theme.SimonYellow

// IMPORTACIONES REQUERIDAS DE TU ARCHIVO Color.kt
import gz.dam.simon_dice.ui.theme.SimonRedDark
import gz.dam.simon_dice.ui.theme.SimonGreenDark
import gz.dam.simon_dice.ui.theme.SimonBlueDark
import gz.dam.simon_dice.ui.theme.SimonYellowDark



/**
 * Clase que almacena los datos del juego
 */
object Datos {
    // Observers para notificar cambios
    private val observers = mutableListOf<(String) -> Unit>()

    fun addObserver(observer: (String) -> Unit) {
        observers.add(observer)
    }

    private fun notifyObservers(event: String) {
        observers.forEach { it(event) }
    }

    // Variables de Estado Reactivas
    private var _ronda by mutableStateOf(0)
    var ronda: Int
        get() = _ronda
        private set(value) {
            _ronda = value
            notifyObservers("RONDA_CHANGED")
        }

    private var _record by mutableStateOf(0)
    var record: Int
        get() = _record
        private set(value) {
            _record = value
            notifyObservers("RECORD_CHANGED")
        }

    private var _gameState by mutableStateOf<GameState>(GameState.Inicio)
    var gameState: GameState
        get() = _gameState
        private set(value) {
            _gameState = value
            notifyObservers("GAME_STATE_CHANGED")
        }

    var text by mutableStateOf("PRESIONA START")
    var mostrarSecuencia by mutableStateOf(false)
    var colorActivo by mutableStateOf(-1)
    var botonesBrillantes by mutableStateOf(false)
    var jugando by mutableStateOf(false)

    // Secuencias
    var secuencia = mutableListOf<Int>()
    var secuenciaUsuario = mutableListOf<Int>()

    // FUNCIONES DE ACCESO CONTROLADO
    fun updateRonda(value: Int) { ronda = value }
    fun updateRecord(value: Int) { record = value }
    fun updateGameState(value: GameState) { gameState = value }
    fun updateText(value: String) { text = value }
    fun updateMostrarSecuencia(value: Boolean) { mostrarSecuencia = value }
    fun updateColorActivo(value: Int) { colorActivo = value }
    fun updateBotonesBrillantes(value: Boolean) { botonesBrillantes = value }
    fun updateJugando(value: Boolean) { jugando = value }

    /**
     * Reinicia el juego al estado inicial
     */
    fun reiniciarJuego() {
        secuencia.clear()
        secuenciaUsuario.clear()
        updateRonda(0)
        updateGameState(GameState.Inicio)
        updateText("PRESIONA START")
        updateMostrarSecuencia(false)
        updateColorActivo(-1)
        updateBotonesBrillantes(false)
        updateJugando(false)
        notifyObservers("GAME_RESET")
    }
}

/**
 * Enum con los colores del juego y sus propiedades
 */
enum class Colores(val colorInt: Int, val nombre: String, val tono: String) {
    ROJO(0, "ROJO", "Mi"),
    VERDE(1, "VERDE", "Do"),
    AZUL(2, "AZUL", "Sol"),
    AMARILLO(3, "AMARILLO", "Do'")
}

// Función de extensión para obtener el color base (color CLARO/BRILLANTE, activo)
fun Colores.baseColor(): Color {
    return when (this) {
        Colores.ROJO -> SimonRed
        Colores.VERDE -> SimonGreen
        Colores.AZUL -> SimonBlue
        Colores.AMARILLO -> SimonYellow
    }
}

// Función de extensión para obtener el color oscurecido (color OSCURO/INACTIVO)
fun Colores.colorOscurecido(): Color {
    return when (this) {
        Colores.ROJO -> SimonRedDark
        Colores.VERDE -> SimonGreenDark
        Colores.AZUL -> SimonBlueDark
        Colores.AMARILLO -> SimonYellowDark
    }
}