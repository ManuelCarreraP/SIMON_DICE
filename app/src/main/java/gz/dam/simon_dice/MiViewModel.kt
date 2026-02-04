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

    private val database = AppDatabase.getDatabase(application)
    private val repository = RecordRepository(database.recordDao())

    private var currentPlayerName = "Manuel"

    private val _record = MutableStateFlow(0)
    val record: StateFlow<Int> = _record.asStateFlow()

    private val _recordTexto = MutableStateFlow("Sin récord")
    val recordTexto: StateFlow<String> = _recordTexto.asStateFlow()

    private val _recordParaRecuadro = MutableStateFlow("0")
    val recordParaRecuadro: StateFlow<String> = _recordParaRecuadro.asStateFlow()
    private val _playerInfo = MutableStateFlow("Jugador: $currentPlayerName")
    val playerInfo: StateFlow<String> = _playerInfo.asStateFlow()

    init {
        cargarRecordGuardado()
        observarTodosRecords()
    }

    private fun cargarRecordGuardado() {
        viewModelScope.launch {
            val bestRecord = repository.getBestRecord()

            if (bestRecord != null) {
                _record.value = bestRecord.score
                _recordParaRecuadro.value = bestRecord.score.toString()

                val date = Date(bestRecord.timestamp)
                val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                _recordTexto.value = "${bestRecord.playerName}: ${bestRecord.score} ($fecha $hora)"
                _playerInfo.value = "Jugando como: $currentPlayerName | Mejor: ${bestRecord.playerName}"
            } else {
                _record.value = 0
                _recordParaRecuadro.value = "0"
                _recordTexto.value = "Sin récord"
                _playerInfo.value = "Jugando como: $currentPlayerName"
            }
        }
    }

    private fun observarTodosRecords() {
        viewModelScope.launch {
            repository.getAllRecordsFlow().collect { records ->
                if (records.isNotEmpty()) {
                    val bestRecord = records.maxByOrNull { it.score }
                    bestRecord?.let {
                        _record.value = it.score
                        _recordParaRecuadro.value = it.score.toString()

                        val date = Date(it.timestamp)
                        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                        val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
                        _recordTexto.value = "${it.playerName}: ${it.score} ($fecha $hora)"
                        _playerInfo.value = "Jugando como: $currentPlayerName | Mejor: ${it.playerName}"

                        android.util.Log.d("Room_Player", "Mejor record actual: ${it.playerName}: ${it.score}")
                    }
                }
            }
        }
    }

    suspend fun verificarYActualizarRecord(posibleRecord: Int): Boolean {
        val currentMaxScore = repository.getMaxScore()

        if (posibleRecord > currentMaxScore) {
            repository.saveRecord(posibleRecord, currentPlayerName)

            _record.value = posibleRecord
            _recordParaRecuadro.value = posibleRecord.toString()

            val now = Date()
            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(now)
            val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            // Mostrar nombre del jugador junto al nuevo record
            _recordTexto.value = "$currentPlayerName: $posibleRecord ($fecha $hora)"
            _playerInfo.value = "¡Nuevo récord! $currentPlayerName: $posibleRecord"

            android.util.Log.d("Room_Player", "Nuevo record de $currentPlayerName: $posibleRecord")

            return true
        }
        return false
    }


    fun clearRecords() {
        viewModelScope.launch {
            repository.clearRecords()
            _record.value = 0
            _recordParaRecuadro.value = "0"
            _recordTexto.value = "Sin récord"
            _playerInfo.value = "Jugando como: $currentPlayerName (sin records)"
        }
    }
}