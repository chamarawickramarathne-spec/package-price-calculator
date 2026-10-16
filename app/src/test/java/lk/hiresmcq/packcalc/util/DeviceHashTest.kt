package lk.hiresmcq.packcalc.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceHashTest {

    @Test
    fun `hash is deterministic`() {
        val a = DeviceHash.of("test-id")
        val b = DeviceHash.of("test-id")
        assertEquals(a, b)
    }

    @Test
    fun `hash is 32 hex chars`() {
        val h = DeviceHash.of("abc")
        assertEquals(32, h.length)
        assertTrue(h.all { it.isDigit() || it.isLowerCase() })
    }

    @Test
    fun `different inputs produce different hashes`() {
        val a = DeviceHash.of("id1")
        val b = DeviceHash.of("id2")
        assertTrue(a != b)
    }

    @Test
    fun `known vector sha256(abc) first 32`() {
        // SHA-256("abc") -> ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad
        val h = DeviceHash.of("abc")
        assertTrue(h.startsWith("ba7816bf8f01cfea414140de5dae2223"))
    }

    @Test
    fun `handles empty string`() {
        val h = DeviceHash.of("")
        assertEquals(32, h.length)
    }

    @Test
    fun `handles unicode`() {
        val h = DeviceHash.of("ហៃ\u200d\u200d")
        assertEquals(32, h.length)
    }
}
