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
        assertEquals("My Tagline", BusinessProfile(tagline = " My Tagline ").displayTagline)
    }

    @Test
    fun blankTaglineFallsBackToBuiltInTagline() {
        assertEquals(CompanyInfo.TAGLINE, BusinessProfile().displayTagline)
        assertEquals(CompanyInfo.TAGLINE, BusinessProfile(tagline = "  ").displayTagline)
    }

    @Test
    fun contactLineIsNullWhenAllFieldsBlank() {
        assertNull(BusinessProfile().contactLine)
        assertNull(BusinessProfile(phone = " ", email = "", address = "\t").contactLine)
    }

    @Test
    fun contactLineJoinsOnlyNonBlankFields() {
        assertEquals(
            "+94 77 123 4567",
            BusinessProfile(phone = "+94 77 123 4567").contactLine,
        )
        assertEquals(
            "+94 77 123 4567 \u2022 Colombo, Sri Lanka",
            BusinessProfile(phone = "+94 77 123 4567", address = "Colombo, Sri Lanka").contactLine,
        )
        assertEquals(
            "+94 77 123 4567 \u2022 studio@example.com \u2022 Colombo",
            BusinessProfile(
                phone = "+94 77 123 4567",
                email = "studio@example.com",
                address = "Colombo",
            ).contactLine,
        )
    }
}
