package lk.hiresmcq.packcalc.domain

import java.math.BigDecimal

/** Lightweight row for the Saved Packages list. */
data class SavedPackageSummary(
    val id: Long,
    val packageName: String,
    val clientName: String?,
    val projectDate: String?,
    val finalTotal: BigDecimal,
    val createdAt: Long,
)

/** A complete, self-contained package (inputs + computed breakdown + item snapshot). */
data class PackageQuote(
    val id: Long,
    val packageName: String,
    val clientName: String?,
    val projectDate: String?,
    val lines: List<EquipmentLine>,
    val laborHours: BigDecimal,
    val hourlyRate: BigDecimal,
    val profitPercentage: BigDecimal,
    val extraCosts: ExtraCosts,
    val breakdown: PricingBreakdown,
    val notes: String?,
    val invoiceNumber: String?,
    val sourceTemplateId: Int?,
    val createdAt: Long,
    val updatedAt: Long,
)

/** Input for creating or updating a saved package. */
data class PackageDraft(
    val id: Long?,
    val packageName: String,
    val clientName: String?,
    val projectDate: String?,
    val lines: List<EquipmentLine>,
    val laborHours: BigDecimal,
    val hourlyRate: BigDecimal,
    val profitPercentage: BigDecimal,
    val extraCosts: ExtraCosts,
    val notes: String?,
    val sourceTemplateId: Int?,
)
