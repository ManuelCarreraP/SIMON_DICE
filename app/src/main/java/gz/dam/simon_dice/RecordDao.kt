package gz.dam.simon_dice

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Query("SELECT * FROM records WHERE id = 1")
    suspend fun getRecord(): RecordEntity?

    @Query("SELECT * FROM records WHERE id = 1")
    fun getRecordFlow(): Flow<RecordEntity?>  // CORREGIDO: Añadido Flow

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity)

    @Query("SELECT score FROM records WHERE id = 1")
    suspend fun getRecordScore(): Int?

    @Query("DELETE FROM records")
    suspend fun deleteAll()
}