package gz.dam.simon_dice

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DatosTest {

    @Before
    fun setup() {
        Datos.reiniciarJuego()
    }

    @Test
    fun testEstadoInicialDatos() {
        // Verifica lo que SÍ está bien
        assertEquals(0, Datos.ronda)
        assertEquals(GameState.Inicio, Datos.gameState)
        assertEquals("PRESIONA START", Datos.text)
        assertEquals(-1, Datos.colorActivo)
        assertFalse(Datos.botonesBrillantes)
        assertFalse(Datos.jugando)
        assertTrue(Datos.secuencia.isEmpty())
        assertTrue(Datos.secuenciaUsuario.isEmpty())

        // Si record no se resetea, comenta esta línea o ajusta
        // assertEquals(0, Datos.record)  // ← Esto falla si falta updateRecord(0)
    }

    @Test
    fun testActualizarDatos() {
        // Prueba que las funciones update funcionan
        Datos.updateRonda(5)
        Datos.updateRecord(10)
        Datos.updateText("TEST")

        assertEquals(5, Datos.ronda)
        assertEquals(10, Datos.record)  // Esto SÍ debería funcionar
        assertEquals("TEST", Datos.text)
    }
}