package solutions.dreamforge.infrastructure.dto

import kotlinx.serialization.Serializable
import solutions.dreamforge.domain.model.Asset
import solutions.dreamforge.domain.model.AssetCategory
import solutions.dreamforge.domain.model.AssetStatus
import solutions.dreamforge.domain.model.MaintenanceRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Data Transfer Object (DTO) for Asset entities.
 * This class is used for serialization in REST endpoints.
 *
 * The DTO pattern serves several important purposes:
 * 1. Decouples the domain model from the API contract
 * 2. Enables explicit versioning and evolution of the API
 * 3. Optimizes payload size by including only necessary fields
 * 4. Supports serialization with frameworks like kotlinx.serialization
 * 
 * @property id String representation of the asset's UUID
 * @property name Human-readable name of the asset
 * @property description Detailed description of the asset
 * @property acquisitionDate ISO-8601 formatted date string of when the asset was acquired
 * @property acquisitionCost Original cost of the asset at time of acquisition
 * @property currentValue Current estimated value of the asset after depreciation
 * @property location Physical location of the asset
 * @property status String representation of the asset's status
 * @property category String representation of the asset's category
 * @property maintenanceRecords List of maintenance records associated with the asset
 */
@Serializable
data class AssetDto(
    val id: String,
    val name: String,
    val description: String,
    val acquisitionDate: String,
    val acquisitionCost: Double,
    val currentValue: Double,
    val location: String,
    val status: String,
    val category: String,
    val maintenanceRecords: List<MaintenanceRecordDto> = emptyList()
) {
    companion object {
        /**
         * Converts a domain Asset entity to its DTO representation.
         * This method handles the conversion of complex types, like enums and dates,
         * to their string representations for API transfer.
         *
         * @param asset The domain Asset entity to convert
         * @return An AssetDto representation of the domain entity
         */
        fun fromDomain(asset: Asset): AssetDto {
            return AssetDto(
                id = asset.id.toString(),
                name = asset.name,
                description = asset.description,
                acquisitionDate = asset.acquisitionDate.format(DateTimeFormatter.ISO_DATE),
                acquisitionCost = asset.acquisitionCost,
                currentValue = asset.currentValue,
                location = asset.location,
                status = asset.status.name,
                category = asset.category.name,
                maintenanceRecords = asset.maintenanceRecords.map { MaintenanceRecordDto.fromDomain(it) }
            )
        }
    }

    /**
     * Converts this DTO to a domain Asset entity.
     * Note: This is a simplified conversion that cannot fully reconstruct an Asset
     * with its original identity. In a real application, this method would be
     * used in conjunction with repository methods to reconstitute existing entities.
     *
     * @return A new Asset instance with properties from this DTO
     */
    fun toDomain(): Asset {
        val asset = Asset.create(
            name = name,
            description = description,
            acquisitionDate = LocalDate.parse(acquisitionDate),
            acquisitionCost = acquisitionCost,
            location = location,
            category = AssetCategory.valueOf(category)
        )
        
        // This is a simplification as we cannot directly access private constructor
        // In a real application, we would need a different approach to handle this
        // such as a factory method or a repository method to reconstitute the entity
        
        return asset
    }
}

/**
 * DTO for creating a new asset.
 * Separates creation parameters from the full AssetDto to simplify API usage
 * and validation.
 * 
 * @property name Human-readable name of the asset
 * @property description Detailed description of the asset
 * @property acquisitionDate ISO-8601 formatted date string of when the asset was acquired
 * @property acquisitionCost Original cost of the asset at time of acquisition
 * @property location Physical location of the asset
 * @property category String representation of the asset's category
 */
@Serializable
data class CreateAssetRequest(
    val name: String,
    val description: String,
    val acquisitionDate: String,
    val acquisitionCost: Double,
    val location: String,
    val category: String
)

/**
 * DTO for updating an existing asset.
 * All fields are optional to allow partial updates.
 * 
 * @property name Optional new name of the asset
 * @property description Optional new description of the asset
 * @property location Optional new location of the asset
 * @property status Optional new status of the asset
 */
@Serializable
data class UpdateAssetRequest(
    val name: String? = null,
    val description: String? = null,
    val location: String? = null,
    val status: String? = null
)

/**
 * DTO for adding a maintenance record to an asset.
 * 
 * @property date ISO-8601 formatted date string of when the maintenance was performed
 * @property description Detailed description of the maintenance activity
 * @property cost The monetary cost incurred for the maintenance
 * @property performedBy Name or identifier of the person or company who performed the maintenance
 * @property maintenanceType String representation of the maintenance type
 */
@Serializable
data class AddMaintenanceRequest(
    val date: String,
    val description: String,
    val cost: Double,
    val performedBy: String,
    val maintenanceType: String
)
