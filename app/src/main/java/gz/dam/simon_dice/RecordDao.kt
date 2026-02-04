package gz.dam.simon_dice

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Query("SELECT * FROM records ORDER BY score DESC LIMIT 1")
    suspend fun getBestRecord(): RecordEntity?

    @Query("SELECT * FROM records WHERE playerName = :name ORDER BY score DESC LIMIT 1")
    suspend fun getBestRecordByPlayer(name: String): RecordEntity?

    @Query("SELECT * FROM records ORDER BY score DESC")
    fun getAllRecordsFlow(): Flow<List<RecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity)

    @Update
    suspend fun updateRecord(record: RecordEntity)

    @Query("SELECT MAX(score) FROM records")
    suspend fun getMaxScore(): Int?

    @Query("DELETE FROM records")
    suspend fun deleteAll()
}