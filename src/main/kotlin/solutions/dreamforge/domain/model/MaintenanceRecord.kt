package solutions.dreamforge.domain.model

import java.time.LocalDate
import java.util.UUID

/**
 * Represents a maintenance record for an asset.
 * This is a value object within the Asset aggregate.
 *
 * A MaintenanceRecord captures a specific maintenance activity performed on an asset,
 * including details about when it happened, who performed it, and associated costs.
 * As a value object, MaintenanceRecord instances are immutable after creation.
 *
 * This class follows the Value Object pattern from Domain-Driven Design, focusing on
 * the attributes rather than identity. Two maintenance records with identical properties
 * can be considered the same conceptual record.
 *
 * @property id Unique identifier for the maintenance record
 * @property date Date when the maintenance was performed
 * @property description Detailed description of the maintenance activity
 * @property cost The monetary cost incurred for the maintenance
 * @property performedBy Name or identifier of the person or company who performed the maintenance
 * @property maintenanceType Classification of the maintenance activity
 */
data class MaintenanceRecord(
    val id: UUID = UUID.randomUUID(),
    val date: LocalDate,
    val description: String,
    val cost: Double,
    val performedBy: String,
    val maintenanceType: MaintenanceType
)

/**
 * Enumeration of maintenance activity types.
 * Used to classify different approaches to asset maintenance.
 *
 * PREVENTIVE - Scheduled maintenance to prevent failures (e.g., regular servicing)
 * CORRECTIVE - Reactive maintenance to fix issues after they occur
 * PREDICTIVE - Maintenance based on the condition assessment and forecasting
 * CONDITION_BASED - Maintenance performed when specific conditions are met
 */
enum class MaintenanceType {
    PREVENTIVE,
    CORRECTIVE,
    PREDICTIVE,
    CONDITION_BASED
}
