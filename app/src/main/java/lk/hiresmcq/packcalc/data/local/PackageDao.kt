package lk.hiresmcq.packcalc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageDao {

    @Query("SELECT * FROM packages ORDER BY createdAt DESC, id DESC")
    fun observePackages(): Flow<List<PackageEntity>>

    @Query("SELECT * FROM packages WHERE id = :id")
    suspend fun getPackage(id: Long): PackageEntity?

    @Query("SELECT * FROM package_items WHERE packageId = :packageId ORDER BY id ASC")
    suspend fun getItems(packageId: Long): List<PackageItemEntity>

    @Insert
    suspend fun insertPackage(pkg: PackageEntity): Long

    @Update
    suspend fun updatePackage(pkg: PackageEntity)

    @Query("DELETE FROM packages WHERE id = :id")
    suspend fun deletePackage(id: Long)

    @Query("DELETE FROM package_items WHERE packageId = :packageId")
    suspend fun deleteItems(packageId: Long)

    @Insert
    suspend fun insertItems(items: List<PackageItemEntity>)
}
