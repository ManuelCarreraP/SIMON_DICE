package gz.dam.simon_dice

import kotlinx.coroutines.flow.Flow

class RecordRepository(private val recordDao: RecordDao) {

    suspend fun getBestRecord(): RecordEntity? {
        return recordDao.getBestRecord()
    }

    suspend fun getBestRecordByPlayer(name: String): RecordEntity? {
        return recordDao.getBestRecordByPlayer(name)
    }

    suspend fun getMaxScore(): Int {
        return recordDao.getMaxScore() ?: 0
    }

    suspend fun saveRecord(score: Int, playerName: String = "Jugador1") {
        val record = RecordEntity(
            score = score,
            playerName = playerName,
            timestamp = System.currentTimeMillis()
        )
        recordDao.insertRecord(record)
    }

    fun getAllRecordsFlow(): Flow<List<RecordEntity>> {
        return recordDao.getAllRecordsFlow()
    }

    suspend fun clearRecords() {
        recordDao.deleteAll()
    }
}