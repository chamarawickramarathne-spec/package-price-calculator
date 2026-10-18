package lk.hiresmcq.packcalc.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailValidationTest {

    @Test
    fun acceptsTypicalEmail() {
        assertTrue(EmailValidation.isValid("user@example.com"))
        assertTrue(EmailValidation.isValid("first.last+tag@sub.domain.co"))
        assertTrue(EmailValidation.isValid("a_b%c@d.io"))
    }

    @Test
    fun normalizesTrimAndLowercase() {
        assertEquals("user@example.com", EmailValidation.normalize("  User@Example.COM "))
        assertTrue(EmailValidation.isValid("  User@Example.COM "))
    }

    @Test
    fun rejectsEmptyAndBlank() {
        assertFalse(EmailValidation.isValid(""))
        assertFalse(EmailValidation.isValid("   "))
    }

    @Test
    fun rejectsMalformedAddresses() {
        assertFalse(EmailValidation.isValid("plainaddress"))
        assertFalse(EmailValidation.isValid("user@domain"))
        assertFalse(EmailValidation.isValid("user@domain.c"))
        assertFalse(EmailValidation.isValid("user@.com"))
        assertFalse(EmailValidation.isValid("user name@example.com"))
        assertFalse(EmailValidation.isValid("user@example.com."))
        assertFalse(EmailValidation.isValid("user@@example.com"))
    }

    @Test
    fun enforcesDatabaseLengthLimit() {
        // varchar(320) server-side; exactly 320 must pass, more must fail.
        // "@x.com" is 6 chars, so 314 + 6 = 320.
        assertTrue(EmailValidation.isValid("a".repeat(314) + "@x.com"))
        assertFalse(EmailValidation.isValid("a".repeat(315) + "@x.com"))
        assertFalse(EmailValidation.isValid("a".repeat(500) + "@example.com"))
    }
}
