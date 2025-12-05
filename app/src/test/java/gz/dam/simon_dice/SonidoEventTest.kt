package gz.dam.simon_dice

import org.junit.Test
import org.junit.Assert.*

class SonidoEventTest {

    @Test
    fun testTiposSonidoEvent() {
        val colorSound = SonidoEvent.ColorSound(2)
        val error = SonidoEvent.Error
        val victory = SonidoEvent.Victory

        assertTrue(colorSound is SonidoEvent)
        assertTrue(error is SonidoEvent)
        assertTrue(victory is SonidoEvent)
    }

    @Test
    fun testColorSoundContieneColor() {
        val colorSound = SonidoEvent.ColorSound(3)
        assertEquals(3, (colorSound as SonidoEvent.ColorSound).colorInt)
    }
}