package gz.dam.simon_dice

import kotlinx.coroutines.flow.Flow

class RecordRepository(private val recordDao: RecordDao) {

    suspend fun getRecord(): RecordEntity? {
        return recordDao.getRecord()
    }

    suspend fun getRecordScore(): Int {
        return recordDao.getRecordScore() ?: 0
    }

    suspend fun saveRecord(score: Int) {
        val timestamp = System.currentTimeMillis()
        val record = RecordEntity(id = 1, score = score, timestamp = timestamp)
        recordDao.insertRecord(record)
    }

    // CORREGIDO: Usar Flow directamente del DAO
    fun getRecordFlow(): Flow<RecordEntity?> {
        return recordDao.getRecordFlow()
    }

    suspend fun clearRecord() {
        recordDao.deleteAll()
    }
}