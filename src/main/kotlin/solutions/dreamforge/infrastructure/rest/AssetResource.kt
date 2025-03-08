package solutions.dreamforge.infrastructure.rest


import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.runBlocking
import solutions.dreamforge.application.service.AssetService
import solutions.dreamforge.domain.model.AssetCategory
import solutions.dreamforge.domain.model.AssetStatus
import solutions.dreamforge.domain.model.MaintenanceRecord
import solutions.dreamforge.domain.model.MaintenanceType
import solutions.dreamforge.infrastructure.dto.*
import java.time.LocalDate
import java.util.UUID

/**
 * REST resource for asset management.
 * This class provides the HTTP API endpoints for the asset management functionality.
 *
 * Key features:
 * - RESTful design following JAX-RS standards via javax.ws.rs annotations
 * - JSON serialization for request/response
 * - Standard HTTP status codes for responses
 * - Coroutine support for asynchronous handling
 * 
 * All operations delegate to the AssetService, which contains the business logic.
 * This separation maintains clean architecture principles by keeping the HTTP
 * concerns isolated from the application and domain logic.
 * 
 * The class uses the @Path annotation to define the base path for all endpoints,
 * and specifies that it produces and consumes JSON.
 */
@Path("/api/assets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AssetResource @Inject constructor(
    private val assetService: AssetService
) {
    /**
     * Retrieves all assets in the system.
     *
     * HTTP Method: GET
     * Path: /api/assets
     * Response: 200 OK with list of AssetDto objects
     *
     * @return HTTP response containing the list of assets
     */
    @GET
    fun getAllAssets(): Response = runBlocking {
        val assets = assetService.getAllAssets()
        return@runBlocking Response
            .ok(assets.map { AssetDto.fromDomain(it) })
            .build()
    }

    /**
     * Retrieves a specific asset by its ID.
     *
     * HTTP Method: GET
     * Path: /api/assets/{id}
     * Path Param: id - The UUID of the asset to retrieve
     * Response: 
     *   - 200 OK with the AssetDto object if found
     *   - 404 Not Found if the asset with the given ID doesn't exist
     *
     * @param id String representation of the asset's UUID
     * @return HTTP response containing the asset or an error
     */
    @GET
    @Path("/{id}")
    fun getAssetById(@PathParam("id") id: String): Response = runBlocking {
        val asset = assetService.getAssetById(UUID.fromString(id))
            ?: return@runBlocking Response.status(Response.Status.NOT_FOUND).build()
        
        return@runBlocking Response
            .ok(AssetDto.fromDomain(asset))
            .build()
    }

    /**
     * Creates a new asset with the provided details.
     *
     * HTTP Method: POST
     * Path: /api/assets
     * Request Body: CreateAssetRequest object with the asset details
     * Response: 
     *   - 201 Created with the created AssetDto object
     *
     * @param request The request body containing asset creation parameters
     * @return HTTP response containing the created asset
     */
    @POST
    fun createAsset(request: CreateAssetRequest): Response = runBlocking {
        val asset = assetService.createAsset(
            name = request.name,
            description = request.description,
            acquisitionDate = LocalDate.parse(request.acquisitionDate),
            acquisitionCost = request.acquisitionCost,
            location = request.location,
            category = AssetCategory.valueOf(request.category)
        )
        
        return@runBlocking Response
            .status(Response.Status.CREATED)
            .entity(AssetDto.fromDomain(asset))
            .build()
    }

    /**
     * Updates an existing asset with the provided details.
     *
     * HTTP Method: PUT
     * Path: /api/assets/{id}
     * Path Param: id - The UUID of the asset to update
     * Request Body: UpdateAssetRequest object with the fields to update
     * Response: 
     *   - 200 OK with the updated AssetDto object
     *   - 404 Not Found if the asset with the given ID doesn't exist
     *
     * @param id String representation of the asset's UUID
     * @param request The request body containing fields to update
     * @return HTTP response containing the updated asset or an error
     */
    @PUT
    @Path("/{id}")
    fun updateAsset(
        @PathParam("id") id: String,
        request: UpdateAssetRequest
    ): Response = runBlocking {
        val asset = assetService.getAssetById(UUID.fromString(id))
            ?: return@runBlocking Response.status(Response.Status.NOT_FOUND).build()
        
        // Update fields if provided
        request.name?.let { asset.name = it }
        request.description?.let { asset.description = it }
        request.location?.let { asset.transferTo(it) }
        request.status?.let { asset.changeStatus(AssetStatus.valueOf(it)) }
        
        val updatedAsset = assetService.updateAsset(asset)
        
        return@runBlocking Response
            .ok(AssetDto.fromDomain(updatedAsset))
            .build()
    }

    /**
     * Deletes an asset from the system.
     *
     * HTTP Method: DELETE
     * Path: /api/assets/{id}
     * Path Param: id - The UUID of the asset to delete
     * Response: 
     *   - 204 No Content if deletion was successful
     *   - 404 Not Found if the asset with the given ID doesn't exist
     *
     * @param id String representation of the asset's UUID
     * @return HTTP response indicating success or failure
     */
    @DELETE
    @Path("/{id}")
    fun deleteAsset(@PathParam("id") id: String): Response = runBlocking {
        val deleted = assetService.deleteAsset(UUID.fromString(id))
        
        if (!deleted) {
            return@runBlocking Response.status(Response.Status.NOT_FOUND).build()
        }
        
        return@runBlocking Response.noContent().build()
    }

    /**
     * Adds a maintenance record to an asset.
     *
     * HTTP Method: POST
     * Path: /api/assets/{id}/maintenance
     * Path Param: id - The UUID of the asset to which the record will be added
     * Request Body: AddMaintenanceRequest object with the maintenance details
     * Response: 
     *   - 200 OK with the updated AssetDto object
     *   - 404 Not Found if the asset with the given ID doesn't exist
     *
     * @param id String representation of the asset's UUID
     * @param request The request body containing maintenance record details
     * @return HTTP response containing the updated asset or an error
     */
    @POST
    @Path("/{id}/maintenance")
    fun addMaintenanceRecord(
        @PathParam("id") id: String,
        request: AddMaintenanceRequest
    ): Response = runBlocking {
        val maintenanceRecord = MaintenanceRecord(
            date = LocalDate.parse(request.date),
            description = request.description,
            cost = request.cost,
            performedBy = request.performedBy,
            maintenanceType = MaintenanceType.valueOf(request.maintenanceType)
        )
        
        val asset = assetService.addMaintenanceRecord(UUID.fromString(id), maintenanceRecord)
            ?: return@runBlocking Response.status(Response.Status.NOT_FOUND).build()
        
        return@runBlocking Response
            .ok(AssetDto.fromDomain(asset))
            .build()
    }

    /**
     * Marks an asset's maintenance as complete and changes its status back to ACTIVE.
     *
     * HTTP Method: PUT
     * Path: /api/assets/{id}/complete-maintenance
     * Path Param: id - The UUID of the asset to activate
     * Response: 
     *   - 200 OK with the updated AssetDto object
     *   - 404 Not Found if the asset with the given ID doesn't exist
     *
     * @param id String representation of the asset's UUID
     * @return HTTP response containing the updated asset or an error
     */
    @PUT
    @Path("/{id}/complete-maintenance")
    fun completeMaintenanceAndActivate(@PathParam("id") id: String): Response = runBlocking {
        val asset = assetService.completeMaintenanceAndActivate(UUID.fromString(id))
            ?: return@runBlocking Response.status(Response.Status.NOT_FOUND).build()
        
        return@runBlocking Response
            .ok(AssetDto.fromDomain(asset))
            .build()
    }

    /**
     * Applies depreciation to an asset's current value.
     *
     * HTTP Method: PUT
     * Path: /api/assets/{id}/depreciate
     * Path Param: id - The UUID of the asset to depreciate
     * Query Param: rate - The depreciation rate as a decimal (e.g., 0.1 for 10%)
     * Response: 
     *   - 200 OK with the updated AssetDto object
     *   - 404 Not Found if the asset with the given ID doesn't exist
     *
     * @param id String representation of the asset's UUID
     * @param depreciationRate The rate of depreciation
     * @return HTTP response containing the updated asset or an error
     */
    @PUT
    @Path("/{id}/depreciate")
    fun deprecateAsset(
        @PathParam("id") id: String,
        @QueryParam("rate") depreciationRate: Double
    ): Response = runBlocking {
        val asset = assetService.deprecateAsset(UUID.fromString(id), depreciationRate)
            ?: return@runBlocking Response.status(Response.Status.NOT_FOUND).build()
        
        return@runBlocking Response
            .ok(AssetDto.fromDomain(asset))
            .build()
    }

    /**
     * Retrieves assets with a specific operational status.
     *
     * HTTP Method: GET
     * Path: /api/assets/by-status/{status}
     * Path Param: status - The status to filter by (e.g., ACTIVE, IN_MAINTENANCE)
     * Response: 200 OK with list of matching AssetDto objects
     *
     * @param status String representation of the asset status
     * @return HTTP response containing the list of assets
     */
    @GET
    @Path("/by-status/{status}")
    fun getAssetsByStatus(@PathParam("status") status: String): Response = runBlocking {
        val assets = assetService.getAssetsByStatus(AssetStatus.valueOf(status))
        
        return@runBlocking Response
            .ok(assets.map { AssetDto.fromDomain(it) })
            .build()
    }

    /**
     * Retrieves assets within a specific category.
     *
     * HTTP Method: GET
     * Path: /api/assets/by-category/{category}
     * Path Param: category - The category to filter by (e.g., IT_EQUIPMENT, VEHICLES)
     * Response: 200 OK with list of matching AssetDto objects
     *
     * @param category String representation of the asset category
     * @return HTTP response containing the list of assets
     */
    @GET
    @Path("/by-category/{category}")
    fun getAssetsByCategory(@PathParam("category") category: String): Response = runBlocking {
        val assets = assetService.getAssetsByCategory(AssetCategory.valueOf(category))
        
        return@runBlocking Response
            .ok(assets.map { AssetDto.fromDomain(it) })
            .build()
    }

    /**
     * Retrieves assets at a specific location.
     *
     * HTTP Method: GET
     * Path: /api/assets/by-location
     * Query Param: location - The location to filter by
     * Response: 200 OK with list of matching AssetDto objects
     *
     * @param location The location to filter by
     * @return HTTP response containing the list of assets
     */
    @GET
    @Path("/by-location")
    fun getAssetsByLocation(@QueryParam("location") location: String): Response = runBlocking {
        val assets = assetService.getAssetsByLocation(location)
        
        return@runBlocking Response
            .ok(assets.map { AssetDto.fromDomain(it) })
            .build()
    }
}
