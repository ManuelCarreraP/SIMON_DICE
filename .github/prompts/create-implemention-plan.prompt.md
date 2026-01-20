---
agent: 'agent'
description: 'Crear un nuevo archivo de plan de implementación para nuevas funcionalidades, refactorización de código existente o actualización de paquetes, diseño, arquitectura o infraestructura.'
tools: ['changes', 'search/codebase', 'edit/editFiles', 'extensions', 'fetch', 'githubRepo', 'openSimpleBrowser', 'problems', 'runTasks', 'search', 'search/searchResults', 'runCommands/terminalLastCommand', 'runCommands/terminalSelection', 'testFailure', 'usages', 'vscodeAPI']
---
# Crear plan de implementación

## Directiva principal

Tu objetivo es crear un nuevo archivo de plan de implementación para `${input:PlanPurpose}`. Tu salida debe ser legible por máquinas, determinista y estructurada para la ejecución autónoma por otros sistemas de IA o por humanos.
Utiliza el idioma "Español" para redactar el plan.

## Contexto de ejecución

Este prompt está diseñado para comunicación AI-a-AI y procesamiento automatizado. Todas las instrucciones deben interpretarse literalmente y ejecutarse de forma sistemática sin interpretación o aclaración humana.

## Requisitos principales

- Generar planes de implementación que sean totalmente ejecutables por agentes IA o humanos
- Usar lenguaje determinista sin ambigüedad
- Estructurar todo el contenido para análisis y ejecución automatizada
- Asegurar autotención completa sin dependencias externas para su comprensión

## Requisitos de estructura del plan

Los planes deben consistir en fases discretas y atómicas que contengan tareas ejecutables. Cada fase debe ser procesable de forma independiente por agentes IA o humanos sin dependencias entre fases a menos que se indiquen explícitamente.

## Arquitectura de fases

- Cada fase debe tener criterios de finalización medibles
- Las tareas dentro de las fases deben poder ejecutarse en paralelo a menos que se especifique una dependencia
- Todas las descripciones de tareas deben incluir rutas de archivo específicas, nombres de funciones y detalles exactos de implementación
- Ninguna tarea debe requerir interpretación o toma de decisiones humana

## Estándares de implementación optimizados para IA

- Usar lenguaje explícito y sin ambigüedades que no requiera interpretación
- Estructurar todo el contenido en formatos legibles por máquina (tablas, listas, datos estructurados)
- Incluir rutas de archivo específicas, números de línea y referencias de código exactas cuando corresponda
- Definir todas las variables, constantes y valores de configuración de forma explícita
- Proporcionar contexto completo dentro de cada descripción de tarea
- Usar prefijos estandarizados para todos los identificadores (REQ-, TASK-, etc.)
- Incluir criterios de validación que puedan verificarse automáticamente

## Especificaciones del archivo de salida

- Guardar los archivos de plan de implementación en el directorio `/plan/`
- Usar la convención de nombres: `[purpose]-[component]-[version].md`
- Prefijos de propósito: `upgrade|refactor|feature|data|infrastructure|process|architecture|design`
- Ejemplo: `upgrade-system-command-4.md`, `feature-auth-module-1.md`
- El archivo debe ser Markdown válido con estructura front matter adecuada

## Estructura de plantilla obligatoria

Todas las plantillas de plan de implementación deben seguir estrictamente la plantilla siguiente. Cada sección es obligatoria y debe rellenarse con contenido específico y accionable. Los agentes IA deben validar el cumplimiento de la plantilla antes de la ejecución.

## Reglas de validación de la plantilla

- Todos los campos del front matter deben estar presentes y correctamente formateados
- Todos los encabezados de sección deben coincidir exactamente (sensible a mayúsculas)
- Los prefijos de identificador deben seguir el formato especificado
- Las tablas deben incluir todas las columnas requeridas
- No debe quedar texto de marcador de posición en la versión final

## Estado

El estado del plan de implementación debe definirse claramente en el front matter y debe reflejar el estado actual del plan. El estado puede ser uno de los siguientes (status_color entre corchetes): `Completed` (insignia verde brillante), `In progress` (amarilla), `Planned` (azul), `Deprecated` (roja) o `On Hold` (naranja). También debe mostrarse como una insignia en la sección de introducción.

```md
---
goal: [Título conciso que describa el objetivo del plan de implementación]
version: [Opcional: p. ej., 1.0, Fecha]
date_created: [YYYY-MM-DD]
last_updated: [Opcional: YYYY-MM-DD]
owner: [Opcional: Equipo/Persona responsable de esta especificación]
status: 'Completed'|'In progress'|'Planned'|'Deprecated'|'On Hold'
tags: [Opcional: Lista de etiquetas o categorías relevantes, p. ej., `feature`, `upgrade`, `chore`, `architecture`, `migration`, `bug` etc]
---

# Introducción

![Status: <status>](https://img.shields.io/badge/status-<status>-<status_color>)

[Una breve introducción concisa al plan y al objetivo que pretende alcanzar.]

## 1. Requisitos y restricciones

[Enumerar explícitamente todos los requisitos y restricciones que afectan al plan y condicionan cómo se implementa. Utilizar listas o tablas para mayor claridad.]

- **REQ-001**: Requisito 1
- **SEC-001**: Requisito de seguridad 1
- **[3 LETTERS]-001**: Otro requisito 1
- **CON-001**: Restricción 1
- **GUD-001**: Directriz 1
- **PAT-001**: Patrón a seguir 1

## 2. Pasos de implementación

### Fase de implementación 1

- GOAL-001: [Describir el objetivo de esta fase, p. ej., "Implementar la característica X", "Refactorizar el módulo Y", etc.]

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-001 | Descripción de la tarea 1 | ✅ | 2025-04-25 |
| TASK-002 | Descripción de la tarea 2 | |  |
| TASK-003 | Descripción de la tarea 3 | |  |

### Fase de implementación 2

- GOAL-002: [Describir el objetivo de esta fase, p. ej., "Implementar la característica X", "Refactorizar el módulo Y", etc.]

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-004 | Descripción de la tarea 4 | |  |
| TASK-005 | Descripción de la tarea 5 | |  |
| TASK-006 | Descripción de la tarea 6 | |  |

## 3. Alternativas

[Una lista de viñetas con cualquier enfoque alternativo que se consideró y por qué no se eligió. Esto ayuda a contextualizar y justificar el enfoque elegido.]

- **ALT-001**: Enfoque alternativo 1
- **ALT-002**: Enfoque alternativo 2

## 4. Dependencias

[Enumerar cualquier dependencia que deba abordarse, como bibliotecas, frameworks u otros componentes de los que dependa el plan.]

- **DEP-001**: Dependencia 1
- **DEP-002**: Dependencia 2

## 5. Archivos

[Enumere los archivos que se verán afectados por la funcionalidad o la tarea de refactorización.]

- **FILE-001**: Descripción del archivo 1
- **FILE-002**: Descripción del archivo 2

## 6. Pruebas

[Enumere las pruebas que deben implementarse para verificar la funcionalidad o la tarea de refactorización.]

- **TEST-001**: Descripción de la prueba 1
- **TEST-002**: Descripción de la prueba 2

## 7. Riesgos y supuestos

[Enumere los riesgos o supuestos relacionados con la implementación del plan.]

- **RISK-001**: Riesgo 1
- **ASSUMPTION-001**: Supuesto 1

## 8. Especificaciones relacionadas / Lecturas adicionales

[Enlace a especificación relacionada 1]
[Enlace a documentación externa relevante]
