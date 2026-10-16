package lk.hiresmcq.packcalc.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "package_items",
    foreignKeys = [
        ForeignKey(
            entity = PackageEntity::class,
            parentColumns = ["id"],
            childColumns = ["packageId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("packageId")],
)
data class PackageItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val packageId: Long,
    val equipmentId: Int? = null,
    val name: String,
    val unitValue: Double,
    val quantity: Int,
    val lineTotal: Double,
)
