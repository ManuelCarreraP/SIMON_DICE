# 🐙 GitHub Issues - Templates para Copiar y Pegar

Aquí están los templates listos para crear los 8 issues directamente en GitHub.
Solo necesitas copiar el contenido y pegarlo en cada issue nuevo.

---

## ISSUE 1: [FASE 1] Crear modelos de datos

**Título:**
```
[FASE 1] Crear modelos de datos
```

**Descripción:**
```
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

**Labels:** `feature` `mongodb` `phase-1`
**Milestone:** `v1.1.0`

---

## ISSUE 2: [FASE 2] Implementar Repository Pattern

**Título:**
```
[FASE 2] Implementar Repository Pattern
```

**Descripción:**
```
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
- Completa ISSUE 1 (Modelos)

## DoD
- 3 implementaciones completas
- Todas con observables (Flow)
- Sin errores de compilación
- Tests unitarios para cada una
```

**Labels:** `feature` `mongodb` `phase-2`
**Milestone:** `v1.1.0`

---

## ISSUE 3: [FASE 3] Crear sistema de sincronización

**Título:**
```
[FASE 3] Crear sistema de sincronización
```

**Descripción:**
```
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
- Completa ISSUE 2 (Repository Pattern)

## DoD
- SyncManager sincroniza cada 30s
- Conflictos resueltos correctamente
- Funciona offline (colas locales)
- Tests de sincronización
```

**Labels:** `feature` `mongodb` `phase-3`
**Milestone:** `v1.1.0`

---

## ISSUE 4: [FASE 4] Integrar Repository en ViewModel

**Título:**
```
[FASE 4] Integrar Repository en ViewModel
```

**Descripción:**
```
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
- Completa ISSUE 3 (SyncManager)

## DoD
- ViewModel compila sin errores
- Observables funcionan correctamente
- UI se actualiza con cambios
- Tests del ViewModel
```

**Labels:** `feature` `mongodb` `phase-4`
**Milestone:** `v1.1.0`

---

## ISSUE 5: [FASE 5] Agregar indicadores de sync en UI

**Título:**
```
[FASE 5] Agregar indicadores de sync en UI + Config MongoDB
```

**Descripción:**
```
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
- Completa ISSUE 4 (ViewModel)

## DoD
- Indicador de sync visible en UI
- Botón resincronizar funciona
- build.gradle.kts compila correctamente
- UI se actualiza con estados de sync
```

**Labels:** `feature` `mongodb` `phase-5`
**Milestone:** `v1.1.0`

---

## ISSUE 6: [SETUP] Configurar MongoDB Atlas

**Título:**
```
[SETUP] Configurar MongoDB Atlas y obtener App ID
```

**Descripción:**
```
## Objetivo
Preparar infraestructura de MongoDB Atlas antes de empezar desarrollo.

## Tareas
- [ ] Crear cuenta en MongoDB Atlas (https://cloud.mongodb.com)
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
1-2 horas (puede hacerse paralelo a FASE 1)

## DoD
- Cuenta MongoDB activa
- App ID obtenido
- Realm Sync habilitado
- Autenticación anónima configurada
```

**Labels:** `setup` `mongodb` `documentation`
**Milestone:** `v1.1.0`

---

## ISSUE 7: [TESTING] Tests unitarios e integración

**Título:**
```
[TESTING] Tests unitarios e integración para MongoDB
```

**Descripción:**
```
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

**Labels:** `testing` `mongodb` `quality`
**Milestone:** `v1.1.0`

---

## ISSUE 8: [RELEASE] Preparar v1.1.0

**Título:**
```
[RELEASE] Preparar v1.1.0 - MongoDB Integration
```

**Descripción:**
```
## Objetivo
Preparar la versión 1.1.0 con integración de MongoDB.

## Tareas
- [ ] Actualizar versionCode=2 en build.gradle.kts
- [ ] Actualizar versionName="1.1.0" en build.gradle.kts
- [ ] Crear rama release/1.1 en Git
- [ ] Hacer commit: "bump: v1.0 → v1.1.0"
- [ ] Merge release/1.1 → main
- [ ] Crear tag: `git tag -a v1.1.0 -m "MongoDB integration"`
- [ ] Push: `git push origin main --tags`
- [ ] Merge main → develop
- [ ] Crear Release en GitHub con changelog

## Checklist Final
- [ ] Build APK generado sin errores
- [ ] Todos los tests pasan
- [ ] No hay warnings críticos
- [ ] Documentación actualizada

## DoD
- Tag v1.1.0 creado
- Release publicada en GitHub
- Versiones actualizadas correctamente
```

**Labels:** `release` `version-bump`
**Milestone:** `v1.1.0`

---

## 📋 Instrucciones Rápidas

### Crear en GitHub UI (Manual):
1. Abre tu repo en GitHub
2. Click en **"Issues"**
3. Click en **"New Issue"**
4. Copia el **Título** arriba
5. Copia la **Descripción** arriba
6. Selecciona **Labels** (ver arriba)
7. Selecciona **Milestone**: `v1.1.0`
8. Click **"Submit new issue"**
9. Repite para cada issue

### Con GitHub CLI (Más Rápido):
```bash
# Si no tienes gh instalado:
# macOS: brew install gh
# Linux: sudo apt install gh
# Windows: choco install gh

# Autenticarse
gh auth login

# Crear cada issue
gh issue create \
  -t "[FASE 1] Crear modelos de datos" \
  -b "$(cat descripcion.txt)" \
  -l feature,mongodb,phase-1 \
  -m "v1.1.0"
```

---

**Próximo paso**: Copia cada template arriba y crea los 8 issues en tu repositorio GitHub.


