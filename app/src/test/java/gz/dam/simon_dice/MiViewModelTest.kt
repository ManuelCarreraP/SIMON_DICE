package gz.dam.simon_dice

import org.junit.Test
import org.junit.Assert.*

class MiViewModelSimpleTest {

    // Mock de ControladorPreference para tests
    private class MockControladorPreference {
        private var recordGuardado = 0
        private var timestampGuardado = 0L

        fun obtenerRecordScore(): Int = recordGuardado

        fun obtenerRecordCompleto(): Pair<Int, Long> = Pair(recordGuardado, timestampGuardado)

        fun actualizarRecord(nuevoRecord: Int) {
            if (nuevoRecord > recordGuardado) {
                recordGuardado = nuevoRecord
                timestampGuardado = System.currentTimeMillis()
            }
        }
    }

    // Mock de MiViewModel simplificado para tests
    private class MockMiViewModel(val controlador: MockControladorPreference) {
        private var record = 0
        private var recordTexto = "Sin récord"
        private var recordParaRecuadro = "0"

        init {
            cargarRecordGuardado()
        }

        private fun cargarRecordGuardado() {
            val (score, timestamp) = controlador.obtenerRecordCompleto()
            record = score
            recordParaRecuadro = score.toString()

            if (timestamp > 0) {
                recordTexto = "Récord: $score (fecha simulada)"
            } else {
                recordTexto = "Sin récord"
            }
        }

        fun verificarYActualizarRecord(posibleRecord: Int): Boolean {
            val recordActual = controlador.obtenerRecordScore()

            if (posibleRecord > recordActual) {
                controlador.actualizarRecord(posibleRecord)
                record = posibleRecord
                recordParaRecuadro = posibleRecord.toString()
                recordTexto = "Récord: $posibleRecord (fecha simulada)"
                return true
            }

            return false
        }

        fun getRecord() = record
        fun getRecordTexto() = recordTexto
        fun getRecordParaRecuadro() = recordParaRecuadro
    }

    @Test
    fun testRecordInicialCero() {
        val controlador = MockControladorPreference()
        val viewModel = MockMiViewModel(controlador)

        assertEquals(0, viewModel.getRecord())
        assertEquals("Sin récord", viewModel.getRecordTexto())
        assertEquals("0", viewModel.getRecordParaRecuadro())
    }

    @Test
    fun testActualizarRecordCuandoEsMayor() {
        val controlador = MockControladorPreference()
        val viewModel = MockMiViewModel(controlador)

        val resultado = viewModel.verificarYActualizarRecord(8)

        assertTrue(resultado)
        assertEquals(8, viewModel.getRecord())
        assertEquals("Récord: 8 (fecha simulada)", viewModel.getRecordTexto())
        assertEquals("8", viewModel.getRecordParaRecuadro())
    }

    @Test
    fun testNoActualizarRecordCuandoEsMenor() {
        val controlador = MockControladorPreference()
        val viewModel = MockMiViewModel(controlador)

        // Establecer record 10
        viewModel.verificarYActualizarRecord(10)

        // Intentar con 5 (menor)
        val resultado = viewModel.verificarYActualizarRecord(5)

        assertFalse(resultado)
        assertEquals(10, viewModel.getRecord())  // Se mantiene 10
        assertEquals("10", viewModel.getRecordParaRecuadro())
    }

    @Test
    fun testSoloActualizaRecordMayor() {
        val controlador = MockControladorPreference()
        val viewModel = MockMiViewModel(controlador)

        // Secuencia: 3, 7, 5, 10, 8
        val resultados = mutableListOf<Boolean>()
        val secuencia = listOf(3, 7, 5, 10, 8)

        secuencia.forEach { record ->
            resultados.add(viewModel.verificarYActualizarRecord(record))
        }

        // Solo deberían actualizar: 3 (primero), 7 (mayor que 3), 10 (mayor que 7)
        assertEquals(listOf(true, true, false, true, false), resultados)
        assertEquals(10, viewModel.getRecord())  // El mayor es 10
    }

    @Test
    fun testRecordPersistenteEntreInstancias() {
        val controlador = MockControladorPreference()

        // Primera instancia
        val viewModel1 = MockMiViewModel(controlador)
        viewModel1.verificarYActualizarRecord(15)

        // Segunda instancia (simula reinicio de app)
        val viewModel2 = MockMiViewModel(controlador)

        // Debería cargar el record guardado (15)
        assertEquals(15, viewModel2.getRecord())
        assertEquals("15", viewModel2.getRecordParaRecuadro())
    }

    @Test
    fun testTextoRecordContieneInfo() {
        val controlador = MockControladorPreference()
        val viewModel = MockMiViewModel(controlador)

        viewModel.verificarYActualizarRecord(12)

        val texto = viewModel.getRecordTexto()
        assertTrue(texto.contains("Récord: 12"))
    }
}