package gz.dam.simon_dice

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.Date

object ControladorSQLite {
    private const val DATABASE_NAME = "simon_dice_top10.db"
    private const val DATABASE_VERSION = 2
    private const val TABLE_RECORDS = "records"
    private const val MAX_RECORDS = 10

    private const val KEY_ID = "_id"
    private const val KEY_SCORE = "score"
    private const val KEY_TIMESTAMP = "timestamp"
    private const val KEY_PLAYER_NAME = "player_name"

    class SimonDiceDBHelper(context: Context) : SQLiteOpenHelper(
        context, DATABASE_NAME, null, DATABASE_VERSION
    ) {
        override fun onCreate(db: SQLiteDatabase) {
            val createTable = """
                CREATE TABLE $TABLE_RECORDS (
                    $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $KEY_SCORE INTEGER NOT NULL,
                    $KEY_TIMESTAMP INTEGER NOT NULL,
                    $KEY_PLAYER_NAME TEXT DEFAULT 'Jugador'
                )
            """.trimIndent()
            db.execSQL(createTable)
            Log.d("SQLite_Top10", "Tabla $TABLE_RECORDS creada (TOP 10)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2) {
                db.execSQL("DROP TABLE IF EXISTS $TABLE_RECORDS")
                onCreate(db)
                Log.d("SQLite_Top10", "Base de datos actualizada a v$newVersion")
            }
        }
    }

    fun insertarRecord(context: Context, nuevoScore: Int): Boolean {
        return try {
            val dbHelper = SimonDiceDBHelper(context)
            val db = dbHelper.writableDatabase

            val cursorCount = db.rawQuery("SELECT COUNT(*) FROM $TABLE_RECORDS", null)
            var count = 0
            if (cursorCount.moveToFirst()) count = cursorCount.getInt(0)
            cursorCount.close()

            var peorScore = Int.MAX_VALUE
            var peorTimestamp = Long.MAX_VALUE
            if (count >= MAX_RECORDS) {
                val cursorPeor = db.query(
                    TABLE_RECORDS,
                    arrayOf(KEY_SCORE, KEY_TIMESTAMP),
                    null, null, null, null,
                    "$KEY_SCORE ASC, $KEY_TIMESTAMP DESC", // Peor: menor score, más reciente en empates
                    "1"
                )
                if (cursorPeor.moveToFirst()) {
                    peorScore = cursorPeor.getInt(cursorPeor.getColumnIndexOrThrow(KEY_SCORE))
                    peorTimestamp = cursorPeor.getLong(cursorPeor.getColumnIndexOrThrow(KEY_TIMESTAMP))
                }
                cursorPeor.close()
            }

            val entraEnTop10 = count < MAX_RECORDS ||
                    nuevoScore > peorScore ||
                    (nuevoScore == peorScore && System.currentTimeMillis() < peorTimestamp)

            if (entraEnTop10) {
                if (count >= MAX_RECORDS) {
                    val whereClause = if (nuevoScore > peorScore) {
                        "$KEY_SCORE = ?"
                    } else {
                        "$KEY_SCORE = ? AND $KEY_TIMESTAMP = ?"
                    }

                    val whereArgs = if (nuevoScore > peorScore) {
                        arrayOf(peorScore.toString())
                    } else {
                        arrayOf(peorScore.toString(), peorTimestamp.toString())
                    }

                    db.delete(TABLE_RECORDS, whereClause, whereArgs)
                }

                val values = ContentValues().apply {
                    put(KEY_SCORE, nuevoScore)
                    put(KEY_TIMESTAMP, Date().time)
                    put(KEY_PLAYER_NAME, "Jugador")
                }

                val nuevoId = db.insert(TABLE_RECORDS, null, values)

                android.util.Log.d("SQLite_Game", "¡NUEVO RECORD TOP 10! Score: $nuevoScore")

                db.close()
                return true
            } else {
                Log.d("SQLite_Top10", "Score $nuevoScore no entra en TOP 10")
                db.close()
                return false
            }
        } catch (e: Exception) {
            Log.e("SQLite", "Error al insertar record: ${e.message}")
            return false
        }
    }

    fun obtenerMejorRecordScore(context: Context): Int {
        return try {
            val dbHelper = SimonDiceDBHelper(context)
            val db = dbHelper.readableDatabase

            val cursor = db.query(
                TABLE_RECORDS,
                arrayOf("MAX($KEY_SCORE) as max_score"),
                null, null, null, null, null
            )

            var maxScore = 0
            if (cursor.moveToFirst()) {
                maxScore = cursor.getInt(cursor.getColumnIndexOrThrow("max_score"))
            }

            cursor.close()
            db.close()
            maxScore
        } catch (e: Exception) {
            Log.e("SQLite", "Error al obtener mejor record: ${e.message}")
            0
        }
    }

    // NUEVO: Obtener TOP 10 completo
    fun obtenerTop10Records(context: Context): List<Triple<Int, Long, String>> {
        val records = mutableListOf<Triple<Int, Long, String>>()
        return try {
            val dbHelper = SimonDiceDBHelper(context)
            val db = dbHelper.readableDatabase

            val cursor = db.query(
                TABLE_RECORDS,
                arrayOf(KEY_SCORE, KEY_TIMESTAMP, KEY_PLAYER_NAME),
                null, null, null, null,
                "$KEY_SCORE DESC, $KEY_TIMESTAMP ASC",
                MAX_RECORDS.toString()
            )

            while (cursor.moveToNext()) {
                val score = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TIMESTAMP))
                val playerName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAYER_NAME))
                records.add(Triple(score, timestamp, playerName))
            }

            cursor.close()
            db.close()
            records
        } catch (e: Exception) {
            Log.e("SQLite_Top10", "Error al obtener TOP 10: ${e.message}")
            records
        }
    }

    // El resto de métodos permanecen igual...
    fun obtenerMejorRecordCompleto(context: Context): Triple<Int, Long, String> {
        // Mismo código que antes
        return try {
            val dbHelper = SimonDiceDBHelper(context)
            val db = dbHelper.readableDatabase

            val cursor = db.query(
                TABLE_RECORDS,
                arrayOf(KEY_SCORE, KEY_TIMESTAMP, KEY_PLAYER_NAME),
                null, null, null, null,
                "$KEY_SCORE DESC",
                "1"
            )

            var score = 0
            var timestamp = 0L
            var playerName = "Jugador"

            if (cursor.moveToFirst()) {
                score = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE))
                timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TIMESTAMP))
                playerName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAYER_NAME))
            }

            cursor.close()
            db.close()
            Triple(score, timestamp, playerName)
        } catch (e: Exception) {
            Triple(0, 0L, "Jugador")
        }
    }

    fun obtenerTodosRecords(context: Context): List<Triple<Int, Long, String>> {
        val records = mutableListOf<Triple<Int, Long, String>>()
        return try {
            val dbHelper = SimonDiceDBHelper(context)
            val db = dbHelper.readableDatabase

            val cursor = db.query(
                TABLE_RECORDS,
                arrayOf(KEY_SCORE, KEY_TIMESTAMP, KEY_PLAYER_NAME),
                null, null, null, null,
                "$KEY_SCORE DESC"
            )

            while (cursor.moveToNext()) {
                val score = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TIMESTAMP))
                val playerName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAYER_NAME))
                records.add(Triple(score, timestamp, playerName))
            }

            cursor.close()
            db.close()
            records
        } catch (e: Exception) {
            records
        }
    }

    fun eliminarTodosRecords(context: Context) {
        try {
            val dbHelper = SimonDiceDBHelper(context)
            val db = dbHelper.writableDatabase
            db.delete(TABLE_RECORDS, null, null)
            db.close()
            Log.d("SQLite", "Todos los records eliminados")
        } catch (e: Exception) {
            Log.e("SQLite", "Error al eliminar records: ${e.message}")
        }
    }
}