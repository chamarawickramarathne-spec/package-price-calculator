package lk.hiresmcq.packcalc.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class UsageEventTest {

    @Test
    fun `app_open event`() {
        val e = UsageEvent.AppOpen
        assertEquals("app_open", e.eventType)
        assertEquals("/app/open", e.requestUri)
    }

    @Test
    fun `pdf share event`() {
        val e = UsageEvent.PdfShare
        assertEquals("pdf_export", e.eventType)
        assertEquals("/pdf/share", e.requestUri)
    }

    @Test
    fun `pdf save event`() {
        val e = UsageEvent.PdfSave
        assertEquals("pdf_export", e.eventType)
        assertEquals("/pdf/save", e.requestUri)
    }
}
