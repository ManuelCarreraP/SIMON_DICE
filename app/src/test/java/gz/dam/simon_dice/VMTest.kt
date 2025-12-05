package gz.dam.simon_dice

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlinx.coroutines.runBlocking

class VMTest {

    private lateinit var vm: VM

    @Before
    fun setup() {
        vm = VM()
    }

    // Tests que YA TIENES (básicos)
    @Test
    fun testGeneraNumeroEntre0Y3() {
        repeat(10) {
            val numero = vm.generaNumero()
            assertTrue("Número $numero no está entre 0-3", numero in 0..3)
        }
    }

    @Test
    fun testEstadoInicial() {
        assertEquals(0, vm.ronda.value)
        assertEquals(0, vm.record.value)
        assertEquals("PRESIONA START", vm.text.value)
        assertEquals(-1, vm.colorActivo.value)
        assertFalse(vm.botonesBrillantes.value)
        assertNull(vm.sonidoEvent.value)
        assertEquals(GameState.Inicio, vm.gameState.value)
    }

    @Test
    fun testProcesarClickUsuario_EnEstadoIncorrecto_NoHaceNada() {
        // Cuando NO está en EsperandoJugador, no debe procesar clicks
        val estadoInicial = vm.gameState.value
        val textInicial = vm.text.value

        vm.procesarClickUsuario(0)  // Estado Inicio

        assertEquals(estadoInicial, vm.gameState.value)
        assertEquals(textInicial, vm.text.value)
    }

    @Test
    fun testClearSoundEvent_Funciona() {
        // Debería poder limpiar el evento
        vm.clearSoundEvent()
        assertNull(vm.sonidoEvent.value)
    }
}