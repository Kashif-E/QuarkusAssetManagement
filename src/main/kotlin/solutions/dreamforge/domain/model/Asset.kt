package solutions.dreamforge.domain.model

import java.time.LocalDate
import java.util.UUID

/**
 * Represents a physical asset in the organization.
 * This is our root aggregate in the Asset domain.
 *
 * An Asset is any physical item of value that the organization owns and needs to track.
 * It contains all lifecycle information, including acquisition details, current status,
 * and maintenance history.
 *
 * As the root aggregate, Asset is responsible for maintaining its own consistency
 * and manages all operations on its child entities and value objects.
 *
 * @property id Unique identifier for the asset, generated automatically
 * @property name Human-readable name of the asset
 * @property description Detailed description of the asset
 * @property acquisitionDate Date when the asset was acquired
 * @property acquisitionCost Original cost of the asset at time of acquisition
 * @property currentValue Current estimated value of the asset after depreciation
 * @property location Physical location of the asset
 * @property status Current operational status of the asset
 * @property category Classification category of the asset
 * @property maintenanceRecords List of maintenance history records
 */
class Asset private constructor(
    val id: UUID,
    var name: String,
    var description: String,
    var acquisitionDate: LocalDate,
    var acquisitionCost: Double,
    var currentValue: Double,
    var location: String,
    var status: AssetStatus,
    var category: AssetCategory,
    val maintenanceRecords: MutableList<MaintenanceRecord> = mutableListOf()
) {
    companion object {
        /**
         * Factory method to create a new Asset instance.
         * This is the preferred way to instantiate an Asset as it ensures
         * proper initialization and generates a unique identifier.
         *
         * @param name Human-readable name of the asset
         * @param description Detailed description of the asset
         * @param acquisitionDate Date when the asset was acquired
         * @param acquisitionCost Original cost of the asset at time of acquisition
         * @param location Physical location of the asset
         * @param category Classification category of the asset
         * @return A new Asset instance with default ACTIVE status
         */
        fun create(
            name: String,
            description: String,
            acquisitionDate: LocalDate,
            acquisitionCost: Double,
            location: String,
            category: AssetCategory
        ): Asset {
            return Asset(
                id = UUID.randomUUID(),
                name = name,
                description = description,
                acquisitionDate = acquisitionDate,
                acquisitionCost = acquisitionCost,
                currentValue = acquisitionCost, // Initially the same as acquisition cost
                location = location,
                status = AssetStatus.ACTIVE,
                category = category
            )
        }
    }

    /**
     * Adds a maintenance record to this asset's history.
     * This operation does not automatically change the asset's status.
     *
     * @param record The maintenance record to add
     */
    fun addMaintenanceRecord(record: MaintenanceRecord) {
        maintenanceRecords.add(record)
    }

    /**
     * Applies depreciation to the asset's current value.
     * This reduces the current value by the specified percentage.
     *
     * @param depreciationRate The rate of depreciation as a decimal (e.g., 0.1 for 10%)
     */
    fun depreciate(depreciationRate: Double) {
        currentValue *= (1.0 - depreciationRate)
    }

    /**
     * Updates the asset's location when it is physically transferred.
     *
     * @param newLocation The new physical location of the asset
     */
    fun transferTo(newLocation: String) {
        location = newLocation
    }

    /**
     * Updates the operational status of the asset.
     * This method should be used for all status transitions to ensure
     * proper business rules are applied.
     *
     * @param newStatus The new status to set for the asset
     */
    fun changeStatus(newStatus: AssetStatus) {
        status = newStatus
    }
}

/**
 * Enumeration of possible operational statuses for an asset.
 * Used to track the current state of an asset throughout its lifecycle.
 *
 * ACTIVE - Asset is currently in use and operational
 * IN_MAINTENANCE - Asset is temporarily out of service for maintenance
 * DEPRECATED - Asset is still owned but no longer in active use
 * DISPOSED - Asset has been sold, recycled, or otherwise removed from inventory
 */
enum class AssetStatus {
    ACTIVE,
    IN_MAINTENANCE,
    DEPRECATED,
    DISPOSED
}

/**
 * Enumeration of possible asset categories.
 * Used for classification and reporting purposes.
 *
 * IT_EQUIPMENT - Computing devices, servers, network equipment
 * OFFICE_FURNITURE - Desks, chairs, tables, storage units
 * VEHICLES - Cars, trucks, vans, specialized vehicles
 * MACHINERY - Manufacturing equipment, tools, specialized machinery
 * BUILDINGS - Physical structures
 * LAND - Property parcels
 */
enum class AssetCategory {
    IT_EQUIPMENT,
    OFFICE_FURNITURE,
    VEHICLES,
    MACHINERY,
    BUILDINGS,
    LAND
}
