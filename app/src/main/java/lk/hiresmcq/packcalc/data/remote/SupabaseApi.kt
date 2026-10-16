package lk.hiresmcq.packcalc.data.remote

import lk.hiresmcq.packcalc.data.model.AccessLogDto
import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.model.EquipmentTypeDto
import lk.hiresmcq.packcalc.data.model.TemplateDto
import lk.hiresmcq.packcalc.data.model.TemplateEquipmentDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
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

    // ---- category_types ----
    @Headers("Prefer: return=representation")
    @POST("category_types")
    suspend fun insertCategory(@Body body: CategoryDto): List<CategoryDto>

    // ---- equipment_types ----
    @Headers("Prefer: return=representation")
    @POST("equipment_types")
    suspend fun insertEquipmentType(@Body body: EquipmentTypeDto): List<EquipmentTypeDto>

    // ---- equipment_details ----
    @Headers("Prefer: return=representation")
    @POST("equipment_details")
    suspend fun insertEquipment(@Body body: EquipmentDto): List<EquipmentDto>

    // ---- templates ----
    @Headers("Prefer: return=representation")
    @POST("templates")
    suspend fun insertTemplate(@Body body: TemplateDto): List<TemplateDto>

    @Headers("Prefer: return=minimal")
    @POST("template_equipment")
    suspend fun insertTemplateEquipment(
        @Body body: List<TemplateEquipmentDto>,
    ): List<TemplateEquipmentDto>

    // ---- access_logs (usage logging; append-only) ----
    @Headers("Prefer: return=minimal")
    @POST("access_logs")
    suspend fun insertAccessLog(@Body body: AccessLogDto)
}
