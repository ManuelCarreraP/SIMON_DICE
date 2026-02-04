package gz.dam.simon_dice

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int,
    val playerName: String = "Manuel",
    val timestamp: Long = System.currentTimeMillis()
)