package lk.hiresmcq.packcalc.domain

import lk.hiresmcq.packcalc.util.CompanyInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BusinessProfileTest {

    @Test
    fun blankNameFallsBackToBuiltInStudio() {
        assertEquals(CompanyInfo.NAME, BusinessProfile().displayName)
        assertEquals(CompanyInfo.NAME, BusinessProfile(name = "   ").displayName)
        assertEquals(CompanyInfo.NAME, BusinessProfile(name = "").displayName)
    }

    @Test
    fun customNameIsTrimmedAndUsed() {
        assertEquals("My Studio", BusinessProfile(name = "  My Studio ").displayName)
    }

    @Test
    fun contactLineIsNullWhenAllFieldsBlank() {
        assertNull(BusinessProfile().contactLine)
        assertNull(BusinessProfile(phone = " ", email = "\t").contactLine)
    }

    @Test
    fun contactLineJoinsOnlyNonBlankFields() {
        assertEquals(
            "+94 77 123 4567",
            BusinessProfile(phone = "+94 77 123 4567").contactLine,
        )
        assertEquals(
            "studio@example.com",
            BusinessProfile(email = "studio@example.com").contactLine,
        )
        assertEquals(
            "+94 77 123 4567 \u2022 studio@example.com",
            BusinessProfile(phone = "+94 77 123 4567", email = "studio@example.com").contactLine,
        )
    }
}
