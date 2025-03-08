package solutions.dreamforge.application.service

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import solutions.dreamforge.domain.model.Asset
import solutions.dreamforge.domain.model.AssetCategory
import solutions.dreamforge.domain.model.AssetStatus
import solutions.dreamforge.domain.model.MaintenanceRecord
import solutions.dreamforge.domain.repository.AssetRepository
import java.time.LocalDate
import java.util.UUID

/**
 * Application service for managing assets.
 * Contains the business logic for asset operations.
 *
 * This service acts as a facade between the infrastructure layer (REST, UI, etc.)
 * and the domain model. It orchestrates domain objects and repositories to implement
 * use cases and ensures that domain rules are enforced.
 *
 * All methods are designed as suspension functions to support asynchronous execution,
 * which is important for reactive programming and scalability.
 *
 * The service is marked as @ApplicationScoped to ensure a single instance is shared
 * across the application, following the Singleton pattern. Quarkus Arc's implementation
 * of CDI provides optimized bean instantiation and lifecycle management.
 */
@ApplicationScoped
class AssetService @Inject constructor(
    private val assetRepository: AssetRepository
) {
    /**
     * Creates a new asset with the provided details.
     *
     * @param name Human-readable name of the asset
     * @param description Detailed description of the asset
     * @param acquisitionDate Date when the asset was acquired
     * @param acquisitionCost Original cost of the asset at time of acquisition
     * @param location Physical location of the asset
     * @param category Classification category of the asset
     * @return The newly created and persisted Asset
     */
    suspend fun createAsset(
        name: String,
        description: String,
        acquisitionDate: LocalDate,
        acquisitionCost: Double,
        location: String,
        category: AssetCategory
    ): Asset {
        val asset = Asset.create(
            name = name,
            description = description,
            acquisitionDate = acquisitionDate,
            acquisitionCost = acquisitionCost,
            location = location,
            category = category
        )
        return assetRepository.save(asset)
    }

    /**
     * Retrieves an asset by its unique identifier.
     *
     * @param id The UUID of the asset to retrieve
     * @return The asset if found, or null if not found
     */
    suspend fun getAssetById(id: UUID): Asset? {
        return assetRepository.findById(id)
    }

    /**
     * Retrieves all assets in the system.
     *
     * @return A list of all assets, or an empty list if none exist
     */
    suspend fun getAllAssets(): List<Asset> {
        return assetRepository.findAll()
    }

    /**
     * Updates an existing asset with modified details.
     *
     * @param asset The asset with updated properties
     * @return The updated asset after persistence
     */
    suspend fun updateAsset(asset: Asset): Asset {
        return assetRepository.update(asset)
    }

    /**
     * Removes an asset from the system.
     *
     * @param id The UUID of the asset to delete
     * @return true if the asset was successfully deleted, false if it wasn't found
     */
    suspend fun deleteAsset(id: UUID): Boolean {
        return assetRepository.delete(id)
    }

    /**
     * Records a maintenance activity for an asset and changes its status to IN_MAINTENANCE.
     *
     * @param assetId The UUID of the asset receiving maintenance
     * @param maintenanceRecord The maintenance record to add
     * @return The updated asset after adding the maintenance record, or null if the asset wasn't found
     */
    suspend fun addMaintenanceRecord(assetId: UUID, maintenanceRecord: MaintenanceRecord): Asset? {
        val asset = assetRepository.findById(assetId) ?: return null
        asset.addMaintenanceRecord(maintenanceRecord)
        
        // Change asset status to IN_MAINTENANCE
        asset.changeStatus(AssetStatus.IN_MAINTENANCE)
        
        return assetRepository.update(asset)
    }

    /**
     * Marks an asset's maintenance as complete and changes its status back to ACTIVE.
     *
     * @param assetId The UUID of the asset to activate after maintenance
     * @return The updated asset with ACTIVE status, or null if the asset wasn't found
     */
    suspend fun completeMaintenanceAndActivate(assetId: UUID): Asset? {
        val asset = assetRepository.findById(assetId) ?: return null
        
        // Change asset status back to ACTIVE
        asset.changeStatus(AssetStatus.ACTIVE)
        
        return assetRepository.update(asset)
    }

    /**
     * Applies depreciation to an asset's current value.
     *
     * @param assetId The UUID of the asset to depreciate
     * @param depreciationRate The rate of depreciation as a decimal (e.g., 0.1 for 10%)
     * @return The updated asset with depreciated value, or null if the asset wasn't found
     */
    suspend fun deprecateAsset(assetId: UUID, depreciationRate: Double): Asset? {
        val asset = assetRepository.findById(assetId) ?: return null
        asset.depreciate(depreciationRate)
        
        return assetRepository.update(asset)
    }

    /**
     * Updates an asset's location when it is physically transferred.
     *
     * @param assetId The UUID of the asset being transferred
     * @param newLocation The new physical location of the asset
     * @return The updated asset with the new location, or null if the asset wasn't found
     */
    suspend fun transferAsset(assetId: UUID, newLocation: String): Asset? {
        val asset = assetRepository.findById(assetId) ?: return null
        asset.transferTo(newLocation)
        
        return assetRepository.update(asset)
    }

    /**
     * Retrieves all assets with a specific operational status.
     *
     * @param status The status to filter by
     * @return A list of assets with the specified status, or an empty list if none exist
     */
    suspend fun getAssetsByStatus(status: AssetStatus): List<Asset> {
        return assetRepository.findByStatus(status)
    }

    /**
     * Retrieves all assets within a specific category.
     *
     * @param category The category to filter by
     * @return A list of assets with the specified category, or an empty list if none exist
     */
    suspend fun getAssetsByCategory(category: AssetCategory): List<Asset> {
        return assetRepository.findByCategory(category)
    }

    /**
     * Retrieves all assets at a specific location.
     *
     * @param location The location to filter by
     * @return A list of assets at the specified location, or an empty list if none exist
     */
    suspend fun getAssetsByLocation(location: String): List<Asset> {
        return assetRepository.findByLocation(location)
    }
}
