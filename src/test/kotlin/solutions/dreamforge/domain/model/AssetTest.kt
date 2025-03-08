package solutions.dreamforge.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AssetTest {
    
    @Test
    fun `test asset creation`() {
        // Given
        val name = "Test Asset"
        val description = "This is a test asset"
        val acquisitionDate = LocalDate.now()
        val acquisitionCost = 1000.0
        val location = "Test Location"
        val category = AssetCategory.IT_EQUIPMENT
        
        // When
        val asset = Asset.create(
            name = name,
            description = description,
            acquisitionDate = acquisitionDate,
            acquisitionCost = acquisitionCost,
            location = location,
            category = category
        )
        
        // Then
        assertEquals(name, asset.name)
        assertEquals(description, asset.description)
        assertEquals(acquisitionDate, asset.acquisitionDate)
        assertEquals(acquisitionCost, asset.acquisitionCost)
        assertEquals(acquisitionCost, asset.currentValue) // Initially the same
        assertEquals(location, asset.location)
        assertEquals(AssetStatus.ACTIVE, asset.status) // Default status
        assertEquals(category, asset.category)
        assertTrue(asset.maintenanceRecords.isEmpty())
    }
    
    @Test
    fun `test adding maintenance record`() {
        // Given
        val asset = createTestAsset()
        val maintenanceRecord = MaintenanceRecord(
            date = LocalDate.now(),
            description = "Routine maintenance",
            cost = 100.0,
            performedBy = "Test Technician",
            maintenanceType = MaintenanceType.PREVENTIVE
        )
        
        // When
        asset.addMaintenanceRecord(maintenanceRecord)
        
        // Then
        assertEquals(1, asset.maintenanceRecords.size)
        assertEquals(maintenanceRecord, asset.maintenanceRecords[0])
    }
    
    @Test
    fun `test depreciation`() {
        // Given
        val asset = createTestAsset()
        val initialValue = asset.currentValue
        val depreciationRate = 0.1 // 10%
        
        // When
        asset.depreciate(depreciationRate)
        
        // Then
        assertEquals(initialValue * 0.9, asset.currentValue, 0.001)
    }
    
    @Test
    fun `test transfer to new location`() {
        // Given
        val asset = createTestAsset()
        val newLocation = "New Test Location"
        
        // When
        asset.transferTo(newLocation)
        
        // Then
        assertEquals(newLocation, asset.location)
    }
    
    @Test
    fun `test change status`() {
        // Given
        val asset = createTestAsset()
        val newStatus = AssetStatus.IN_MAINTENANCE
        
        // When
        asset.changeStatus(newStatus)
        
        // Then
        assertEquals(newStatus, asset.status)
    }
    
    private fun createTestAsset(): Asset {
        return Asset.create(
            name = "Test Asset",
            description = "This is a test asset",
            acquisitionDate = LocalDate.now(),
            acquisitionCost = 1000.0,
            location = "Test Location",
            category = AssetCategory.IT_EQUIPMENT
        )
    }
}
