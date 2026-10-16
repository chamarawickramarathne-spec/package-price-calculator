package lk.hiresmcq.packcalc.data.repo

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lk.hiresmcq.packcalc.data.local.AppDatabase
import lk.hiresmcq.packcalc.data.local.PackageEntity
import lk.hiresmcq.packcalc.data.local.PackageItemEntity
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.domain.ExtraCosts
import lk.hiresmcq.packcalc.domain.PackageDraft
import lk.hiresmcq.packcalc.domain.PackageQuote
import lk.hiresmcq.packcalc.domain.PricingBreakdown
import lk.hiresmcq.packcalc.domain.PricingCalculator
import lk.hiresmcq.packcalc.domain.SavedPackageSummary
import java.math.BigDecimal

class LocalPackageRepository(private val database: AppDatabase) {

    private val dao = database.packageDao()

    fun observeSummaries(): Flow<List<SavedPackageSummary>> =
        dao.observePackages().map { rows -> rows.map(::toSummary) }

    suspend fun getQuote(id: Long): PackageQuote? {
        val entity = dao.getPackage(id) ?: return null
        return toQuote(entity, dao.getItems(id))
    }

    suspend fun save(draft: PackageDraft): Long {
        val now = System.currentTimeMillis()
        val breakdown = PricingCalculator.calculate(
            lines = draft.lines,
            laborHours = draft.laborHours,
            hourlyRate = draft.hourlyRate,
            profitPercentage = draft.profitPercentage,
            extraCosts = draft.extraCosts,
        )
        val items = draft.lines.map { line ->
            PackageItemEntity(
                packageId = draft.id ?: 0L,
                equipmentId = line.equipmentId,
                name = line.name,
                unitValue = line.unitValue.toDouble(),
                quantity = line.quantity,
                lineTotal = line.lineTotal.toDouble(),
            )
        }

        return database.withTransaction {
            if (draft.id == null) {
                val entity = buildEntity(draft, breakdown, now, invoiceNumber = null, createdAt = now)
                val id = dao.insertPackage(entity)
                dao.updatePackage(entity.copy(id = id, invoiceNumber = invoiceNumberFor(id)))
                dao.insertItems(items.map { it.copy(packageId = id) })
                id
            } else {
                val existing = dao.getPackage(draft.id)
                val entity = buildEntity(
                    draft = draft,
                    breakdown = breakdown,
                    now = now,
                    invoiceNumber = existing?.invoiceNumber ?: invoiceNumberFor(draft.id),
                    createdAt = existing?.createdAt ?: now,
                ).copy(id = draft.id)
                dao.updatePackage(entity)
                dao.deleteItems(draft.id)
                dao.insertItems(items.map { it.copy(packageId = draft.id) })
                draft.id
            }
        }
    }

    suspend fun delete(id: Long) {
        dao.deletePackage(id)
    }

    private fun buildEntity(
        draft: PackageDraft,
        breakdown: PricingBreakdown,
        now: Long,
        invoiceNumber: String?,
        createdAt: Long,
    ) = PackageEntity(
        packageName = draft.packageName,
        clientName = draft.clientName,
        projectDate = draft.projectDate,
        laborHours = draft.laborHours.toDouble(),
        hourlyRate = draft.hourlyRate.toDouble(),
        profitPercentage = draft.profitPercentage.toDouble(),
        extraGearCost = draft.extraCosts.extraGear.toDouble(),
        transportationCost = draft.extraCosts.transportation.toDouble(),
        assistantPay = draft.extraCosts.assistantPay.toDouble(),
        editingCost = draft.extraCosts.editing.toDouble(),
        additionalCost = draft.extraCosts.additional.toDouble(),
        equipmentTotal = breakdown.equipmentTotal.toDouble(),
        laborTotal = breakdown.laborTotal.toDouble(),
        extraCostsTotal = breakdown.extraCostsTotal.toDouble(),
        subtotal = breakdown.subtotal.toDouble(),
        profitAmount = breakdown.profitAmount.toDouble(),
        finalTotal = breakdown.finalTotal.toDouble(),
        notes = draft.notes,
        invoiceNumber = invoiceNumber,
        sourceTemplateId = draft.sourceTemplateId,
        createdAt = createdAt,
        updatedAt = now,
    )

    private fun toSummary(entity: PackageEntity) = SavedPackageSummary(
        id = entity.id,
        packageName = entity.packageName,
        clientName = entity.clientName,
        projectDate = entity.projectDate,
        finalTotal = BigDecimal.valueOf(entity.finalTotal),
        createdAt = entity.createdAt,
    )

    private fun toQuote(entity: PackageEntity, items: List<PackageItemEntity>): PackageQuote {
        val lines = items.map { item ->
            EquipmentLine(
                equipmentId = item.equipmentId ?: 0,
                name = item.name,
                unitValue = BigDecimal.valueOf(item.unitValue),
                quantity = item.quantity,
            )
        }
        val extraCosts = ExtraCosts(
            extraGear = BigDecimal.valueOf(entity.extraGearCost),
            transportation = BigDecimal.valueOf(entity.transportationCost),
            assistantPay = BigDecimal.valueOf(entity.assistantPay),
            editing = BigDecimal.valueOf(entity.editingCost),
            additional = BigDecimal.valueOf(entity.additionalCost),
        )
        val breakdown = PricingCalculator.calculate(
            lines = lines,
            laborHours = BigDecimal.valueOf(entity.laborHours),
            hourlyRate = BigDecimal.valueOf(entity.hourlyRate),
            profitPercentage = BigDecimal.valueOf(entity.profitPercentage),
            extraCosts = extraCosts,
        )
        return PackageQuote(
            id = entity.id,
            packageName = entity.packageName,
            clientName = entity.clientName,
            projectDate = entity.projectDate,
            lines = lines,
            laborHours = BigDecimal.valueOf(entity.laborHours),
            hourlyRate = BigDecimal.valueOf(entity.hourlyRate),
            profitPercentage = BigDecimal.valueOf(entity.profitPercentage),
            extraCosts = extraCosts,
            breakdown = breakdown,
            notes = entity.notes,
            invoiceNumber = entity.invoiceNumber,
            sourceTemplateId = entity.sourceTemplateId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    private fun invoiceNumberFor(id: Long): String = "INV-" + id.toString().padStart(4, '0')
}
