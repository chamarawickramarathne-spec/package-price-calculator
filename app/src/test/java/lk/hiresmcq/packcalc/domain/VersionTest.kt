package lk.hiresmcq.packcalc.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionTest {

    @Test
    fun parsesPlainVersion() {
        assertEquals(Version(1, 0, 2), Version.parse("1.0.2"))
    }

    @Test
    fun parsesTagWithPrefixAndSuffix() {
        assertEquals(Version(1, 2, 3), Version.parse("v1.2.3-beta.1"))
    }

    @Test
    fun parsesPartialVersion() {
        assertEquals(Version(2, 1, 0), Version.parse("2.1"))
    }

    @Test
    fun rejectsInvalidInput() {
        assertNull(Version.parse("latest"))
        assertNull(Version.parse(""))
        assertNull(Version.parse(null))
    }

    @Test
    fun newerVersionComparesGreater() {
        assertTrue(Version.parse("1.0.3")!! > Version.parse("1.0.2")!!)
        assertTrue(Version.parse("1.1.0")!! > Version.parse("1.0.9")!!)
        assertTrue(Version.parse("2.0.0")!! > Version.parse("1.9.9")!!)
    }

    @Test
    fun equalVersionComparesZero() {
        assertEquals(0, Version.parse("1.0.2")!!.compareTo(Version(1, 0, 2)))
    }
}
