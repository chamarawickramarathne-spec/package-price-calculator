package lk.hiresmcq.packcalc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packages")
data class PackageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val packageName: String,
    val clientName: String? = null,
    val projectDate: String? = null,
    val laborHours: Double = 0.0,
    val hourlyRate: Double = 0.0,
    val profitPercentage: Double = 0.0,
    val extraGearCost: Double = 0.0,
    val transportationCost: Double = 0.0,
    val assistantPay: Double = 0.0,
    val editingCost: Double = 0.0,
    val additionalCost: Double = 0.0,
    val equipmentTotal: Double = 0.0,
    val laborTotal: Double = 0.0,
    val extraCostsTotal: Double = 0.0,
    val subtotal: Double = 0.0,
    val profitAmount: Double = 0.0,
    val finalTotal: Double = 0.0,
    val notes: String? = null,
    val invoiceNumber: String? = null,
    val sourceTemplateId: Int? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)
