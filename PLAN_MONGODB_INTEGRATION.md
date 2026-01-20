# 📋 PLAN: Integración MongoDB + SharedPreferences + SQLite

## 🎯 Objetivo
Sincronizar records de Simon Dice entre 3 capas de almacenamiento:
- **SharedPreferences**: Configuración local (MANTENER)
- **SQLite**: Caché local rápido (MANTENER)
- **MongoDB**: Nube + sync multi-dispositivo (NUEVO)

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────┐
│      UI (Jetpack Compose)       │
├─────────────────────────────────┤
│    ViewModel (MiViewModel)      │
├─────────────────────────────────┤
│   IRecordRepository (interface) │
├─────────────┬──────────┬────────┤
│ SharedPref  │ SQLite   │MongoDB │
│ (config)    │ (cache)  │(cloud) │
└─────────────┴──────────┴────────┘
     ↓    Sincronización automática ↓
```

---

## 📦 Stack Tecnológico

| Componente | Librería | Versión |
|-----------|----------|---------|
| Sincronización | MongoDB Realm | 1.13.0+ |
| Serialización | Kotlinx Serialization | 1.6.0+ |
| Async | Coroutines | 1.7.1+ |
| SQLite | Room | 2.6.1+ |

---

## 📝 5 Fases de Implementación

### FASE 1: Modelos de Datos (2h)
**Crear**:
- `domain/model/Record.kt` - Modelo base
- `data/model/RealmRecord.kt` - Compatible con Realm
- `domain/model/SyncStatus.kt` - Estados de sincronización

**Código**:
```kotlin
// Record.kt
data class Record(
    val id: String = UUID.randomUUID().toString(),
    val puntos: Int,
    val fecha: Long = System.currentTimeMillis(),
    val nivel: Int = 1,
    val duracion: Long = 0,
    val usuario: String = ""
)

// SyncStatus.kt
enum class SyncStatus { SYNCED, SYNCING, ERROR, OFFLINE, PENDING }
```

---

### FASE 2: Repository Pattern (3h)
**Crear interfaces y implementaciones**:

```kotlin
// domain/repository/IRecordRepository.kt
interface IRecordRepository {
    suspend fun guardarRecord(record: Record): Result<Unit>
    suspend fun obtenerRecords(): Result<List<Record>>
    suspend fun obtenerTop10(): Result<List<Record>>
    suspend fun sincronizar(): Result<Unit>
    fun observarSincronizacion(): Flow<SyncStatus>
    fun observarRecords(): Flow<List<Record>>
}
```

**Implementaciones**:
1. `SharedPreferencesRecordRepository` - Usar JSON + Serialization
2. `SQLiteRecordRepository` - Usar Room DAO
3. `MongoDBRecordRepository` - Usar Realm Sync

**Cada una CRUD + observables con Flow**

---

### FASE 3: Sincronización (2h)
**Crear**:
- `data/sync/SyncManager.kt` - Coordina las 3 fuentes cada 30s
- `data/sync/SyncState.kt` - Estados del sync
- `data/repository/RecordRepository.kt` - Wrapper que usa los 3

**Lógica**:
```
1. Guardar record
   ├─ SharedPreferences (instant)
   ├─ SQLite (instant)
   └─ MongoDB (async)

2. Cada 30s: SyncManager
   ├─ Lee MongoDB
   ├─ Lee SQLite
   ├─ Resuelve conflictos (timestamp)
   └─ Actualiza ambas fuentes
```

---

### FASE 4: Integración ViewModel (2h)
**Modificar**:
- `MiViewModel.kt` - Inyectar RecordRepository
- Observar cambios with Flow
- Exponer SyncStatus al UI

```kotlin
class MiViewModel(private val recordRepository: IRecordRepository) {
    val records = recordRepository.observarRecords()
    val syncStatus = recordRepository.observarSincronizacion()
    
    fun guardarRecord(puntos: Int, nivel: Int) {
        viewModelScope.launch {
            recordRepository.guardarRecord(Record(puntos = puntos, nivel = nivel))
        }
    }
}
```

---

### FASE 5: UI + Config (2h)
**Modificar UI.kt**:
- Indicador de sync (✓ Sincronizado, ⧓ Sincronizando, ✗ Error)
- Botón "Resincronizar"
- Mostrar estado

**Crear config**:
```kotlin
// config/MongoDBConfig.kt
object MongoDBConfig {
    const val MONGODB_APP_ID = BuildConfig.MONGODB_APP_ID
    const val SYNC_INTERVAL_MS = 30000L
}
```

**Actualizar build.gradle.kts**:
```kotlin
dependencies {
    implementation("io.realm.kotlin:library-base:1.13.0")
    implementation("io.realm.kotlin:library-sync:1.13.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
```

---

## 📋 Checklist de Implementación

### Fase 1: Modelos
- [ ] Record.kt creado
- [ ] RealmRecord.kt creado
- [ ] SyncStatus.kt creado
- [ ] Conversiones entre modelos

### Fase 2: Repository
- [ ] IRecordRepository.kt (interfaz)
- [ ] SharedPreferencesRecordRepository.kt
- [ ] SQLiteRecordRepository.kt (DAO + queries)
- [ ] MongoDBRecordRepository.kt
- [ ] Todos tienen observables (Flow)

### Fase 3: Sync
- [ ] SyncManager.kt - Sincronización cada 30s
- [ ] SyncState.kt - Estados
- [ ] RecordRepository.kt - Wrapper principal
- [ ] Resolución de conflictos por timestamp

### Fase 4: ViewModel
- [ ] MiViewModel actualizado
- [ ] Inyectar RecordRepository
- [ ] observarRecords() expuesto
- [ ] observarSincronizacion() expuesto

### Fase 5: UI + Config
- [ ] SyncStatusIndicator Composable
- [ ] MongoDBConfig.kt con App ID
- [ ] build.gradle.kts actualizado
- [ ] UI.kt actualizado

---

## 🔐 MongoDB Setup (Previo)

1. Crear cuenta MongoDB Atlas
2. Crear cluster M0 (gratuito)
3. Crear App Services → Habilitar Realm Sync
4. Obtener App ID → Guardar en `BuildConfig.MONGODB_APP_ID`
5. Configurar autenticación anónima

**Referencia**: https://www.mongodb.com/docs/realm/sdk/kotlin/

---

## 🔀 Git Workflow

```bash
# 1. Feature branch
git checkout -b feature/mongodb-integration

# 2. Commits por fase
git commit -m "feat: crear modelos Record + SyncStatus"
git commit -m "feat: implementar Repository pattern (3 implementations)"
git commit -m "feat: agregar SyncManager"
git commit -m "refactor: integrar Repository en ViewModel"
git commit -m "feat: indicadores de sync en UI"

# 3. Merge a develop
git checkout develop
git merge feature/mongodb-integration

# 4. Release
git checkout -b release/1.1
# Actualizar versionCode=2, versionName="1.1.0"
git commit -m "bump: v1.0 → v1.1.0"
git checkout main
git merge release/1.1
git tag -a v1.1.0 -m "MongoDB integration"
```

---

## ⚠️ Consideraciones Importantes

| Aspecto | Solución |
|--------|----------|
| **Offline** | SQLite + SharedPref funcionan sin internet, MongoDB sincroniza cuando hay conexión |
| **Conflictos** | Usar `ultimaModificacion` timestamp, ganador es el más reciente |
| **Seguridad** | App ID en BuildConfig, nunca en strings hardcodeados |
| **Performance** | Caché en SQLite, MongoDB solo para sync |
| **Migración** | Detectar datos existentes en SharedPref, importar automáticamente |

---

## 📊 Timeline Estimado

| Fase | Duración | Acumulado |
|------|----------|-----------|
| 1. Modelos | 2h | 2h |
| 2. Repository | 3h | 5h |
| 3. Sync | 2h | 7h |
| 4. ViewModel | 2h | 9h |
| 5. UI + Config | 2h | 11h |
| **TOTAL** | **11h** | **11h** |

**+ Testing + docs: 5h más**
**Total con todo: ~16h**

---

## ✅ Criterios de Aceptación

- [ ] Records se guardan en 3 capas (SharedPref, SQLite, MongoDB)
- [ ] Sincronización automática cada 30s
- [ ] Funciona offline (usa SQLite)
- [ ] Indicadores visuales de sync en UI
- [ ] Conflictos resueltos automáticamente
- [ ] No hay pérdida de datos
- [ ] Versión actualizada a 1.1.0

---

## 📚 Referencias Rápidas

| Necesidad | Recurso |
|-----------|---------|
| MongoDB Realm Android | https://www.mongodb.com/docs/realm/sdk/kotlin/ |
| Room Database | https://developer.android.com/training/data-storage/room |
| Kotlin Coroutines | https://kotlinlang.org/docs/coroutines-overview.html |
| Jetpack Compose | https://developer.android.com/jetpack/compose |

---

**Status: ✅ LISTO PARA IMPLEMENTAR**
**Última actualización: 20 Enero 2026**

---

## 🐙 Issues de GitHub

Crear estos issues en tu repositorio GitHub para seguimiento:

### ISSUE 1: [FASE 1] Crear modelos de datos (Record, RealmRecord, SyncStatus)
```
Title: [FASE 1] Crear modelos de datos
Labels: feature, mongodb, phase-1
Assignee: [Tu nombre]
Milestone: v1.1.0

Description:
## Objetivo
Crear los modelos de datos necesarios para la sincronización entre 3 capas de almacenamiento.

## Tareas
- [ ] Crear `domain/model/Record.kt` - Modelo base con @Serializable
- [ ] Crear `data/model/RealmRecord.kt` - Modelo compatible con Realm (@RealmObject)
- [ ] Crear `domain/model/SyncStatus.kt` - Enum con estados (SYNCED, SYNCING, ERROR, OFFLINE, PENDING)
- [ ] Implementar conversiones entre Record ↔ RealmRecord
- [ ] Validar que Record tenga todos los campos necesarios

## Estimación
2 horas

## Dependencias
Ninguna - Es la primera fase

## DoD (Definition of Done)
- Código compilable sin errores
- Modelos con documentación
- Tests unitarios básicos
```

---

### ISSUE 2: [FASE 2] Implementar Repository Pattern (3 implementaciones)
```
Title: [FASE 2] Implementar Repository Pattern
Labels: feature, mongodb, phase-2
Assignee: [Tu nombre]
Milestone: v1.1.0

Description:
## Objetivo
Crear la interfaz IRecordRepository y sus 3 implementaciones para abstraer el almacenamiento.

## Tareas
- [ ] Crear `domain/repository/IRecordRepository.kt` con métodos CRUD + sync + observables
- [ ] Crear `data/repository/implementations/SharedPreferencesRecordRepository.kt`
  - [ ] guardarRecord() con JSON serialization
  - [ ] obtenerRecords() con deserialization
  - [ ] observarRecords() Flow
  - [ ] observarSincronizacion() Flow
- [ ] Crear `data/repository/implementations/SQLiteRecordRepository.kt`
  - [ ] Crear DAO para Room
  - [ ] Implementar CRUD con Room
  - [ ] Observables con Flow
- [ ] Crear `data/repository/implementations/MongoDBRecordRepository.kt`
  - [ ] Configurar Realm Sync
  - [ ] Implementar CRUD con Realm
  - [ ] Observables con Flow

## Estimación
3 horas

## Dependencias
- ISSUE 1 (Modelos)

## DoD
- 3 implementaciones completas
- Todas con observables (Flow)
- Sin errores de compilación
- Tests unitarios para cada una
```

---

### ISSUE 3: [FASE 3] Crear sistema de sincronización (SyncManager)
```
Title: [FASE 3] Crear sistema de sincronización
Labels: feature, mongodb, phase-3
Assignee: [Tu nombre]
Milestone: v1.1.0

Description:
## Objetivo
Implementar SyncManager que coordine la sincronización entre las 3 capas automáticamente cada 30s.

## Tareas
- [ ] Crear `data/sync/SyncState.kt` - Estados de sincronización
- [ ] Crear `data/sync/SyncManager.kt`
  - [ ] Sincronización automática cada 30s con Coroutines
  - [ ] Lógica: Leer MongoDB → Leer SQLite → Resolver conflictos → Actualizar ambos
  - [ ] Resolución de conflictos por timestamp (gana el más reciente)
  - [ ] Manejo de errores y reconnect
- [ ] Crear `data/repository/RecordRepository.kt` - Wrapper principal
  - [ ] Coordina las 3 implementaciones
  - [ ] Delega guardar a las 3 capas
  - [ ] Sync automático

## Estimación
2 horas

## Dependencias
- ISSUE 2 (Repository Pattern)

## DoD
- SyncManager sincroniza cada 30s
- Conflictos resueltos correctamente
- Funciona offline (colas locales)
- Tests de sincronización
```

---

### ISSUE 4: [FASE 4] Integrar Repository en ViewModel
```
Title: [FASE 4] Integrar Repository en ViewModel
Labels: feature, mongodb, phase-4
Assignee: [Tu nombre]
Milestone: v1.1.0

Description:
## Objetivo
Modificar MiViewModel para usar IRecordRepository en lugar de acceso directo.

## Tareas
- [ ] Actualizar `MiViewModel.kt`
  - [ ] Inyectar RecordRepository en constructor
  - [ ] Crear StateFlow de records desde repository
  - [ ] Crear StateFlow de syncStatus desde repository
  - [ ] Implementar guardarRecord() usando repository
  - [ ] Implementar forzarSincronizacion()
- [ ] Actualizar `MiViewModelFactory.kt` si es necesario
- [ ] Verificar que todas las funciones del ViewModel sigan trabajando

## Estimación
2 horas

## Dependencias
- ISSUE 3 (SyncManager)

## DoD
- ViewModel compila sin errores
- Observables funcionan correctamente
- UI se actualiza con cambios
- Tests del ViewModel
```

---

### ISSUE 5: [FASE 5] Agregar indicadores de sync en UI
```
Title: [FASE 5] Agregar indicadores de sync en UI + Config MongoDB
Labels: feature, mongodb, phase-5
Assignee: [Tu nombre]
Milestone: v1.1.0

Description:
## Objetivo
Actualizar UI para mostrar estado de sincronización y configurar MongoDB.

## Tareas
- [ ] Crear `config/MongoDBConfig.kt`
  - [ ] Constantes de configuración
  - [ ] App ID desde BuildConfig
  - [ ] SYNC_INTERVAL_MS = 30000L
- [ ] Actualizar `build.gradle.kts`
  - [ ] Agregar MongoDB Realm 1.13.0+
  - [ ] Agregar Kotlinx Serialization
  - [ ] Agregar Room si falta
- [ ] Actualizar `UI.kt`
  - [ ] Crear SyncStatusIndicator Composable
  - [ ] Mostrar ✓ Sincronizado / ⧓ Sincronizando / ✗ Error
  - [ ] Agregar botón "Resincronizar"
  - [ ] Mostrar timestamp de última sincronización

## Estimación
2 horas

## Dependencias
- ISSUE 4 (ViewModel)

## DoD
- Indicador de sync visible en UI
- Botón resincronizar funciona
- build.gradle.kts compila correctamente
- UI se actualiza con estados de sync
```

---

### ISSUE 6: [SETUP] Configurar MongoDB Atlas
```
Title: [SETUP] Configurar MongoDB Atlas y obtener App ID
Labels: setup, mongodb, documentation
Assignee: [Tu nombre]
Milestone: v1.1.0

Description:
## Objetivo
Preparar infraestructura de MongoDB Atlas antes de empezar desarrollo.

## Tareas
- [ ] Crear cuenta en MongoDB Atlas
- [ ] Crear cluster M0 (gratuito)
- [ ] Crear App Services
- [ ] Habilitar Realm Sync
- [ ] Configurar autenticación anónima
- [ ] Obtener App ID
- [ ] Crear variable BuildConfig.MONGODB_APP_ID
- [ ] Documentar pasos en local.properties

## Referencias
https://www.mongodb.com/docs/realm/sdk/kotlin/

## Estimación
1-2 horas (paralelo a FASE 1)

## DoD
- Cuenta MongoDB activa
- App ID obtenido
- Realm Sync habilitado
- Autenticación anónima configurada
```

---

### ISSUE 7: [TESTING] Tests unitarios y de integración
```
Title: [TESTING] Tests unitarios e integración para MongoDB
Labels: testing, mongodb, quality
Assignee: [Tu nombre]
Milestone: v1.1.0

Description:
## Objetivo
Agregar tests para validar sincronización y repositories.

## Tareas
- [ ] Crear `src/test/java/.../repository/RecordRepositoryTest.kt`
  - [ ] Tests para SharedPreferencesRecordRepository
  - [ ] Tests para SQLiteRecordRepository
  - [ ] Tests para MongoDBRecordRepository
- [ ] Crear `src/test/java/.../sync/SyncManagerTest.kt`
  - [ ] Test sincronización cada 30s
  - [ ] Test resolución de conflictos
  - [ ] Test manejo offline
- [ ] Crear `src/androidTest/java/.../integration/MongoDBIntegrationTest.kt`
  - [ ] Test flujo completo
  - [ ] Test migración de datos

## Estimación
2-3 horas (después de FASE 5)

## DoD
- Cobertura > 70%
- Todos los tests pasan
- No hay warnings en tests
```

---

### ISSUE 8: [RELEASE] Preparar v1.1.0 - MongoDB Integration
```
Title: [RELEASE] Preparar v1.1.0 - MongoDB Integration
Labels: release, version-bump
Assignee: [Tu nombre]
Milestone: v1.1.0

Description:
## Objetivo
Preparar la versión 1.1.0 con integración de MongoDB.

## Tareas
- [ ] Actualizar versionCode=2 en build.gradle.kts
- [ ] Actualizar versionName="1.1.0" en build.gradle.kts
- [ ] Crear rama release/1.1
- [ ] Hacer commit: "bump: v1.0 → v1.1.0"
- [ ] Crear rama main si no existe
- [ ] Merge release/1.1 → main
- [ ] Crear tag: git tag -a v1.1.0 -m "MongoDB integration"
- [ ] Push: git push origin main --tags
- [ ] Merge main → develop

## Checklist Final
- [ ] Build APK generado sin errores
- [ ] All tests pasan
- [ ] No hay warnings críticos
- [ ] Documentación actualizada

## DoD
- Tag v1.1.0 creado
- Release publicada en GitHub
- Versiones actualizadas correctamente
```

---

## 📋 Pasos para Crear los Issues

### Opción 1: Manual en GitHub UI
1. Ir a tu repositorio en GitHub
2. Click en "Issues"
3. Click en "New Issue"
4. Copiar título y descripción de cada issue arriba
5. Asignar labels y milestone
6. Crear issue

### Opción 2: Usando GitHub CLI (más rápido)
```bash
# Instalar gh si no lo tienes
# brew install gh (Mac) o sudo apt install gh (Linux)

# Autenticarse
gh auth login

# Crear issues
gh issue create -t "[FASE 1] Crear modelos de datos" \
  -b "Crear domain/model/Record.kt, data/model/RealmRecord.kt, domain/model/SyncStatus.kt" \
  -l "feature,mongodb,phase-1" \
  -m "v1.1.0"

# ... repetir para cada issue
```

### Opción 3: Script para crear todos de una vez
```bash
#!/bin/bash
# Guardar como create-issues.sh

issues=(
  "[FASE 1] Crear modelos de datos"
  "[FASE 2] Implementar Repository Pattern"
  "[FASE 3] Crear sistema de sincronización"
  "[FASE 4] Integrar Repository en ViewModel"
  "[FASE 5] Agregar indicadores de sync en UI"
  "[SETUP] Configurar MongoDB Atlas"
  "[TESTING] Tests unitarios e integración"
  "[RELEASE] Preparar v1.1.0"
)

for issue in "${issues[@]}"; do
  gh issue create -t "$issue" -l "mongodb,feature" -m "v1.1.0"
done
```

---

## 📊 Orden de Ejecución

```
SETUP (paralelo)
  ├─ ISSUE 6: MongoDB Atlas

FASE 1 (2h)
  └─ ISSUE 1: Modelos

FASE 2 (3h)
  └─ ISSUE 2: Repository Pattern

FASE 3 (2h)
  └─ ISSUE 3: SyncManager

FASE 4 (2h)
  └─ ISSUE 4: ViewModel

FASE 5 (2h)
  └─ ISSUE 5: UI + Config

TESTING (2-3h)
  └─ ISSUE 7: Tests

RELEASE
  └─ ISSUE 8: Release v1.1.0
```

---

**Próximo paso**: Crear los 8 issues en GitHub y empezar con ISSUE 1 y ISSUE 6 en paralelo.
