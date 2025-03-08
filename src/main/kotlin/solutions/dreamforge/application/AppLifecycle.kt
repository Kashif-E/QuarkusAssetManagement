package solutions.dreamforge.application

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.runBlocking
import org.jboss.logging.Logger
import solutions.dreamforge.application.service.AssetService
import solutions.dreamforge.domain.model.AssetCategory
import solutions.dreamforge.domain.model.MaintenanceRecord
import solutions.dreamforge.domain.model.MaintenanceType
import java.time.LocalDate

/**
 * Application lifecycle manager.
 * This class handles application startup and shutdown events.
 *
 * The @ApplicationScoped annotation uses Quarkus Arc's CDI implementation to ensure 
 * a single instance is shared across the application, following the Singleton pattern.
 *
 * It utilizes event-driven programming via CDI events to:
 * 1. Initialize sample data when the application starts
 * 2. Perform cleanup operations when the application shuts down
 *
 * This approach leverages Quarkus' CDI implementation (ArC) which is optimized for
 * faster startup and reduced memory footprint compared to full CDI implementations.
 */
@ApplicationScoped
class AppLifecycle @Inject constructor(
    private val assetService: AssetService,
    private val logger: Logger
) {
    /**
     * Handles application startup event.
     * This method is called when the Quarkus application starts up,
     * after all CDI beans are initialized but before processing requests.
     *
     * For development purposes, it populates the system with sample data.
     * In a production environment, this would likely be omitted or modified
     * to perform different initialization tasks.
     *
     * @param event The StartupEvent triggered by Quarkus
     */
    fun onStart(@Observes event: StartupEvent) {
        logger.info("The application is starting...")
        
        // Initialize sample data
        runBlocking {
            initializeSampleData()
        }
    }

    /**
     * Handles application shutdown event.
     * This method is called when the Quarkus application is shutting down,
     * allowing for graceful cleanup of resources.
     *
     * @param event The ShutdownEvent triggered by Quarkus
     */
    fun onStop(@Observes event: ShutdownEvent) {
        logger.info("The application is stopping...")
    }

    /**
     * Initializes sample data for development purposes.
     * This method creates a set of example assets and maintenance records
     * to provide a realistic testing environment for the application.
     *
     * The method is marked as private since it's an implementation detail
     * and not part of the public API of this class.
     */
    private suspend fun initializeSampleData() {
        logger.info("Initializing sample data...")
        
        // Create sample assets
        val laptop = assetService.createAsset(
            name = "MacBook Pro 2023",
            description = "Developer laptop with 32GB RAM, 1TB SSD",
            acquisitionDate = LocalDate.of(2023, 6, 15),
            acquisitionCost = 2499.99,
            location = "IT Department, Floor 3",
            category = AssetCategory.IT_EQUIPMENT
        )
        
        val vehicle = assetService.createAsset(
            name = "Toyota Camry 2022",
            description = "Company car for executive travel",
            acquisitionDate = LocalDate.of(2022, 3, 10),
            acquisitionCost = 29500.00,
            location = "Company Garage, Slot B12",
            category = AssetCategory.VEHICLES
        )
        
        val serverRack = assetService.createAsset(
            name = "Dell PowerEdge Server Rack",
            description = "Primary database server rack with redundant power",
            acquisitionDate = LocalDate.of(2021, 11, 5),
            acquisitionCost = 62000.00,
            location = "Data Center, Zone A",
            category = AssetCategory.IT_EQUIPMENT
        )
        
        // Add maintenance records
        assetService.addMaintenanceRecord(
            assetId = serverRack.id,
            maintenanceRecord = MaintenanceRecord(
                date = LocalDate.of(2023, 2, 15),
                description = "Quarterly maintenance and cooling system check",
                cost = 1200.00,
                performedBy = "TechCare Services",
                maintenanceType = MaintenanceType.PREVENTIVE
            )
        )
        
        assetService.addMaintenanceRecord(
            assetId = vehicle.id,
            maintenanceRecord = MaintenanceRecord(
                date = LocalDate.of(2023, 5, 10),
                description = "30,000 mile service and tire replacement",
                cost = 850.00,
                performedBy = "AutoCare Center",
                maintenanceType = MaintenanceType.PREVENTIVE
            )
        )
        
        // Complete maintenance for vehicle
        assetService.completeMaintenanceAndActivate(vehicle.id)
        
        // Depreciate laptop
        assetService.deprecateAsset(laptop.id, 0.2) // 20% depreciation
        
        logger.info("Sample data initialization complete")
    }
}
