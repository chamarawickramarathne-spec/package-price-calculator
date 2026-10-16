package lk.hiresmcq.packcalc.data.remote

import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.model.EquipmentTypeDto
import lk.hiresmcq.packcalc.data.model.PackageDto
import lk.hiresmcq.packcalc.data.model.PackageEquipmentDto
import lk.hiresmcq.packcalc.data.model.TemplateDto
import lk.hiresmcq.packcalc.data.model.TemplateEquipmentDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    // ---- reads ----
    @GET("category_types")
    suspend fun getCategoryTypes(@Query("order") order: String): List<CategoryDto>

    @GET("equipment_types")
    suspend fun getEquipmentTypes(@Query("order") order: String): List<EquipmentTypeDto>

    @GET("equipment_details")
    suspend fun getEquipment(
        @Query("order") order: String,
        @Query("is_active") isActive: String?,
        @Query("id") id: String?,
    ): List<EquipmentDto>

    @GET("templates")
    suspend fun getTemplates(
        @Query("order") order: String,
        @Query("is_active") isActive: String?,
    ): List<TemplateDto>

    @GET("template_equipment")
    suspend fun getTemplateEquipment(
        @Query("order") order: String,
        @Query("template_id") templateId: String?,
    ): List<TemplateEquipmentDto>

    @GET("packages")
    suspend fun getPackages(@Query("order") order: String): List<PackageDto>

    @GET("package_equipment")
    suspend fun getPackageEquipment(
        @Query("order") order: String,
        @Query("package_id") packageId: String?,
    ): List<PackageEquipmentDto>

    // ---- category_types ----
    @Headers("Prefer: return=representation")
    @POST("category_types")
    suspend fun insertCategory(@Body body: CategoryDto): List<CategoryDto>

    @Headers("Prefer: return=representation")
    @PATCH("category_types")
    suspend fun updateCategory(@Query("id") idFilter: String, @Body body: CategoryDto): List<CategoryDto>

    // ---- equipment_types ----
    @Headers("Prefer: return=representation")
    @POST("equipment_types")
    suspend fun insertEquipmentType(@Body body: EquipmentTypeDto): List<EquipmentTypeDto>

    @Headers("Prefer: return=representation")
    @PATCH("equipment_types")
    suspend fun updateEquipmentType(
        @Query("id") idFilter: String,
        @Body body: EquipmentTypeDto,
    ): List<EquipmentTypeDto>

    // ---- equipment_details ----
    @Headers("Prefer: return=representation")
    @POST("equipment_details")
    suspend fun insertEquipment(@Body body: EquipmentDto): List<EquipmentDto>

    @Headers("Prefer: return=representation")
    @PATCH("equipment_details")
    suspend fun updateEquipment(
        @Query("id") idFilter: String,
        @Body body: EquipmentDto,
    ): List<EquipmentDto>

    // ---- packages ----
    @Headers("Prefer: return=representation")
    @POST("packages")
    suspend fun insertPackage(@Body body: PackageDto): List<PackageDto>

    @Headers("Prefer: return=representation")
    @PATCH("packages")
    suspend fun updatePackage(
        @Query("id") idFilter: String,
        @Body body: PackageDto,
    ): List<PackageDto>

    @DELETE("packages")
    suspend fun deletePackage(@Query("id") idFilter: String)

    @Headers("Prefer: return=representation")
    @POST("package_equipment")
    suspend fun insertPackageEquipment(
        @Body body: List<PackageEquipmentDto>,
    ): List<PackageEquipmentDto>

    @DELETE("package_equipment")
    suspend fun deletePackageEquipment(@Query("package_id") packageIdFilter: String)

    // ---- templates ----
    @Headers("Prefer: return=representation")
    @POST("templates")
    suspend fun insertTemplate(@Body body: TemplateDto): List<TemplateDto>

    @Headers("Prefer: return=representation")
    @PATCH("templates")
    suspend fun updateTemplate(
        @Query("id") idFilter: String,
        @Body body: TemplateDto,
    ): List<TemplateDto>

    @Headers("Prefer: return=representation")
    @POST("template_equipment")
    suspend fun insertTemplateEquipment(
        @Body body: List<TemplateEquipmentDto>,
    ): List<TemplateEquipmentDto>

    @DELETE("template_equipment")
    suspend fun deleteTemplateEquipment(@Query("template_id") templateIdFilter: String)
}
