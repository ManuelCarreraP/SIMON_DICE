package gz.dam.simon_dice

import org.junit.Test
import org.junit.Assert.*

class GameStateTest {

    @Test
    fun testTodosLosEstados() {
        assertTrue(GameState.Inicio is GameState)
        assertTrue(GameState.Preparando is GameState)
        assertTrue(GameState.MostrandoSecuencia is GameState)
        assertTrue(GameState.EsperandoJugador is GameState)
        assertTrue(GameState.ProcesandoInput is GameState)
        assertTrue(GameState.SecuenciaCorrecta is GameState)
        assertTrue(GameState.GameOver(5) is GameState)
    }

    @Test
    fun testGameOverContieneRonda() {
        val gameOver = GameState.GameOver(10)
        assertEquals(10, (gameOver as GameState.GameOver).rondaAlcanzada)
    }
}