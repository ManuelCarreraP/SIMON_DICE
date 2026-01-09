# 📱 Simon Dice - Juego con Persistencia de Records
## 🎮 Descripción del Proyecto
Simon Dice es un juego clásico de memoria implementado en Android con Jetpack Compose. El juego genera secuencias de colores que el jugador debe memorizar y repetir. La aplicación incluye persistencia de datos para guardar el récord del jugador entre sesiones.

## 🏆 Sistema de Persistencia de Records
## 🔧 Kapt (Kotlin Annotation Processing Tool)
¿Qué es Kapt?
Kapt es el procesador de anotaciones de Kotlin que permite a las bibliotecas como Room generar código en tiempo de compilación a partir de las anotaciones en tu código.

Cómo funciona en este proyecto:
```bash
kotlin
// En build.gradle.kts
id("kotlin-kapt") // Plugin activado

dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // Kapt procesa las anotaciones de Room
}
``` 
### Flujo de trabajo de Kapt:

Anotaciones: Marcas tu código con @Entity, @Dao, @Database

Procesamiento: Kapt lee estas anotaciones durante la compilación

Generación de código: Crea clases implementadas automáticamente

Compilación: El código generado se incluye en tu APK final

Archivos que usa Kapt en este proyecto:

RecordEntity.kt → Anotado con @Entity

RecordDao.kt → Anotado con @Dao

AppDatabase.kt → Anotado con @Database

## 💾 Arquitectura de Persistencia
Componentes de Room:
```bash
┌─────────────────────────────────────────┐
│           UI (Jetpack Compose)          │
├─────────────────────────────────────────┤
│             ViewModel (VM)              │ ← Controla la lógica del juego
├─────────────────────────────────────────┤
│      ViewModel de Records (MiViewModel) │ ← Maneja el récord persistente
├─────────────────────────────────────────┤
│         Repositorio (RecordRepository)  │ ← Capa de abstracción de datos
├─────────────────────────────────────────┤
│              DAO (RecordDao)            │ ← Operaciones de base de datos
├─────────────────────────────────────────┤
│           Base de Datos (Room)          │ ← SQLite embebida
└─────────────────────────────────────────┘
```
### Flujo de guardado del récord:
Juego finaliza → Se obtiene el puntaje

ViewModel verifica → verificarYActualizarRecord()

Repositorio guarda → saveRecord(score)

DAO ejecuta SQL → INSERT OR REPLACE

Room persiste → Almacena en SQLite

Estado reactivo → UI se actualiza automáticamente
```bash
// 1. Marcas tu clase como entidad
@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey val id: Int = 1,
    val score: Int,
    val timestamp: Long = Date().time
)

// 2. Kapt genera el código SQL automáticamente:
// INSERT OR REPLACE INTO records VALUES (1, 15, 1678901234567)
```
## 📊 Ventajas del Sistema Actual
### Ventajas de usar Kapt con Room:
Verificación en tiempo de compilación: Errores de SQL se detectan al compilar

Código boilerplate reducido: Room genera implementaciones automáticamente

Migraciones sencillas: Manejo fácil de cambios en esquema

Corrutinas integradas: Soporte nativo para operaciones asíncronas

Observación reactiva: StateFlow actualiza la UI automáticamente

## 🎯 Características del sistema de records:
Un solo récord: Solo se guarda el mejor puntaje

Timestamp incluido: Fecha y hora del récord

Persistencia total: Sobrevive a reinicios de app y dispositivo

Actualización automática: UI se refresca sin necesidad de recargar

## 🛠️ Cómo funciona el guardado
```bash
kotlin
// Cuando el jugador supera un récord:
fun verificarYActualizarRecord(posibleRecord: Int): Boolean {
    if (posibleRecord > recordActual) {
        viewModelScope.launch {
            repository.saveRecord(posibleRecord) // ← Aquí se guarda
            // Room + Kapt generan el código SQL automáticamente
        }
        return true
    }
    return false
}
```
La operación genera este SQL:
```bash
sql
INSERT OR REPLACE INTO records (id, score, timestamp) 
VALUES (1, 15, 1678901234567)
```
## 📱 Interfaz de Usuario
La UI muestra:

Ronda actual: Número de secuencia actual

Mejor récord: Máxima ronda alcanzada (en rojo)

Récord persistente: Mejor puntaje histórico con fecha/hora

Estado del juego: Indicador visual del estado actual
