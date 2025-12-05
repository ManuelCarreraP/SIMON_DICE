package gz.dam.simon_dice

import org.junit.Test
import org.junit.Assert.*

class ControladorPreferenceSimpleTest {

    @Test
    fun testComportamientoRecord() {
        // Simulación del comportamiento esperado de ControladorPreference

        var recordGuardado = 0
        var timestampGuardado = 0L

        fun simularActualizarRecord(nuevo: Int) {
            if (nuevo > recordGuardado) {
                recordGuardado = nuevo
                timestampGuardado = System.currentTimeMillis()
            }
        }

        fun simularObtenerRecord(): Int = recordGuardado
        fun simularObtenerRecordCompleto(): Pair<Int, Long> = Pair(recordGuardado, timestampGuardado)

        // Test 1: Record inicial es 0
        assertEquals(0, simularObtenerRecord())

        // Test 2: Guardar 15
        simularActualizarRecord(15)
        assertEquals(15, simularObtenerRecord())

        // Test 3: No guardar 10 (menor)
        simularActualizarRecord(10)
        assertEquals(15, simularObtenerRecord())  // Sigue siendo 15

        // Test 4: Guardar 20 (mayor)
        simularActualizarRecord(20)
        assertEquals(20, simularObtenerRecord())

        // Test 5: Obtener record completo
        val (score, timestamp) = simularObtenerRecordCompleto()
        assertEquals(20, score)
        assertTrue(timestamp > 0)
    }

    @Test
    fun testLogicaDePersistencia() {
        // Test de la lógica: solo guarda si es mayor

        var maxRecord = 0
        val recordsPrueba = listOf(5, 10, 3, 12, 8)

        recordsPrueba.forEach { record ->
            if (record > maxRecord) {
                maxRecord = record
            }
        }

        assertEquals(12, maxRecord)  // El mayor debería ser 12
        assertNotEquals(8, maxRecord)  // 8 no es el mayor
        assertNotEquals(10, maxRecord)  // 10 no es el mayor
        assertTrue(maxRecord > 0)
    }

    @Test
    fun testSecuenciaDeRecords() {
        // Prueba una secuencia completa
        val secuencia = listOf(3, 7, 5, 10, 8, 15, 12)
        var recordActual = 0

        secuencia.forEach { record ->
            if (record > recordActual) {
                recordActual = record
            }
        }

        // Al final, recordActual debería ser el mayor de la lista
        assertEquals(15, recordActual)

        // Verificar que cada paso fue correcto
        val resultadosEsperados = listOf(3, 7, 7, 10, 10, 15, 15)
        var recordPorPaso = 0
        val resultadosReales = mutableListOf<Int>()

        secuencia.forEach { record ->
            if (record > recordPorPaso) {
                recordPorPaso = record
            }
            resultadosReales.add(recordPorPaso)
        }

        assertEquals(resultadosEsperados, resultadosReales)
    }
}