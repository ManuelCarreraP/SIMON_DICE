package gz.dam.simon_dice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MiViewModel(application: Application) : AndroidViewModel(application) {

    private val _record = MutableStateFlow(0)
    val record: StateFlow<Int> = _record.asStateFlow()

    private val _recordTexto = MutableStateFlow("Sin récord (SQLite TOP 10)")
    val recordTexto: StateFlow<String> = _recordTexto.asStateFlow()

    private val _recordParaRecuadro = MutableStateFlow("0")
    val recordParaRecuadro: StateFlow<String> = _recordParaRecuadro.asStateFlow()

    private val _dbInfo = MutableStateFlow("Base de datos: SQLite TOP 10")
    val dbInfo: StateFlow<String> = _dbInfo.asStateFlow()

    init {
        cargarRecordGuardado()
        mostrarTop10EnLogcat()
    }

    private fun cargarRecordGuardado() {
        viewModelScope.launch {
            val (score, timestamp, playerName) = ControladorSQLite.obtenerMejorRecordCompleto(getApplication())
            _record.value = score
            _recordParaRecuadro.value = score.toString()

            if (score > 0) {
                val date = Date(timestamp)
                val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                _recordTexto.value = "Récord: $score ($fecha $hora)"
                _dbInfo.value = "SQLite TOP 10 - Máximo: $score"
            } else {
                _recordTexto.value = "Sin récord (SQLite TOP 10)"
                _dbInfo.value = "Base de datos: SQLite TOP 10 - Vacía"
            }
        }
    }

    fun verificarYActualizarRecord(posibleRecord: Int): Boolean {
        val recordActual = ControladorSQLite.obtenerMejorRecordScore(getApplication())
        _record.value = recordActual
        _recordParaRecuadro.value = recordActual.toString()

        val resultado = ControladorSQLite.insertarRecord(getApplication(), posibleRecord)

        if (resultado) {
            viewModelScope.launch {
                cargarRecordGuardado()

                mostrarTop10EnLogcat()

                val now = Date()
                val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(now)
                val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
                _recordTexto.value = "¡NUEVO TOP 10! $posibleRecord ($fecha $hora)"
                _dbInfo.value = "SQLite TOP 10 - Nuevo record: $posibleRecord"
            }
            return true
        } else {
            _record.value = recordActual
            _recordParaRecuadro.value = recordActual.toString()
            return false
        }
    }

    private fun mostrarTop10EnLogcat() {
        viewModelScope.launch {
            val top10 = ControladorSQLite.obtenerTop10Records(getApplication())
            android.util.Log.d("SQLite_Top10", "=== TOP 10 RECORDS ===")
            top10.forEachIndexed { index, (score, timestamp, player) ->
                val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(Date(timestamp))
                android.util.Log.d("SQLite_Top10", "${index + 1}. $player: $score ($fecha)")
            }
            android.util.Log.d("SQLite_Top10", "======================")
        }
    }

    fun testSQLiteOperations() {
        viewModelScope.launch {
            android.util.Log.d("SQLite_Test", "=== INICIANDO PRUEBAS SQLite ===")
            ControladorSQLite.insertarRecord(getApplication(), 5)
            ControladorSQLite.insertarRecord(getApplication(), 3)
            ControladorSQLite.insertarRecord(getApplication(), 8)
            ControladorSQLite.insertarRecord(getApplication(), 2)
            cargarRecordGuardado()
        }
    }
}