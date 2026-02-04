package gz.dam.simon_dice

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.util.Log

@Database(
    entities = [RecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recordDao(): RecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "simon_dice_room_database"
                )
                    .fallbackToDestructiveMigration() // Para desarrollo, en producción usa migraciones
                    .build()

                INSTANCE = instance
                Log.d("RoomDatabase", "Base de datos Room creada: simon_dice_room_database")
                instance
            }
        }

        // Método para cerrar la base de datos (opcional, para testing)
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
            Log.d("RoomDatabase", "Base de datos Room cerrada")
        }

        // Método para limpiar la base de datos (para testing)
        fun clearDatabase(context: Context) {
            closeDatabase()
            INSTANCE = null
            getDatabase(context) // Esto creará una nueva instancia limpia
            Log.d("RoomDatabase", "Base de datos Room limpiada")
        }
    }
}