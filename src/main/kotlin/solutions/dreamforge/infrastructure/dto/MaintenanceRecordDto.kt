package solutions.dreamforge.infrastructure.dto

import kotlinx.serialization.Serializable
import solutions.dreamforge.domain.model.MaintenanceRecord
import solutions.dreamforge.domain.model.MaintenanceType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Data Transfer Object (DTO) for MaintenanceRecord value objects.
 * Used for serialization in REST endpoints.
 *
 * This DTO follows the same pattern as AssetDto, providing a clear
 * separation between domain objects and API representation. It handles
 * conversion of complex types like dates, enums, and UUIDs to and from
 * their string representations for API transfer.
 * 
 * @property id String representation of the maintenance record's UUID
 * @property date ISO-8601 formatted date string of when the maintenance was performed
 * @property description Detailed description of the maintenance activity
 * @property cost The monetary cost incurred for the maintenance
 * @property performedBy Name or identifier of the person or company who performed the maintenance
 * @property maintenanceType String representation of the maintenance type
 */
@Serializable
data class MaintenanceRecordDto(
    val id: String,
    val date: String,
    val description: String,
    val cost: Double,
    val performedBy: String,
    val maintenanceType: String
) {
    companion object {
        /**
         * Converts a domain MaintenanceRecord to its DTO representation.
         * This method handles the conversion of complex types, like enums and dates,
         * to their string representations for API transfer.
         *
         * @param record The domain MaintenanceRecord to convert
         * @return A MaintenanceRecordDto representation of the domain record
         */
        fun fromDomain(record: MaintenanceRecord): MaintenanceRecordDto {
            return MaintenanceRecordDto(
                id = record.id.toString(),
                date = record.date.format(DateTimeFormatter.ISO_DATE),
                description = record.description,
                cost = record.cost,
                performedBy = record.performedBy,
                maintenanceType = record.maintenanceType.name
            )
        }
    }

    /**
     * Converts this DTO to a domain MaintenanceRecord value object.
     * Since MaintenanceRecord is a value object (immutable with no identity),
     * this conversion is straightforward.
     *
     * @return A new MaintenanceRecord with properties from this DTO
     */
    fun toDomain(): MaintenanceRecord {
        return MaintenanceRecord(
            id = UUID.fromString(id),
            date = LocalDate.parse(date),
            description = description,
            cost = cost,
            performedBy = performedBy,
            maintenanceType = MaintenanceType.valueOf(maintenanceType)
        )
    }
}
