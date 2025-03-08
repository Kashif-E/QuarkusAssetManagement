package solutions.dreamforge.application.service

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import solutions.dreamforge.domain.model.Asset
import solutions.dreamforge.domain.model.AssetCategory
import solutions.dreamforge.domain.model.AssetStatus
import solutions.dreamforge.domain.model.MaintenanceRecord
import solutions.dreamforge.domain.model.MaintenanceType
import solutions.dreamforge.domain.repository.AssetRepository
import java.time.LocalDate
import java.util.UUID

class AssetServiceTest {
    
    private lateinit var assetRepository: AssetRepository
    private lateinit var assetService: AssetService
    
    @BeforeEach
    fun setup() {
        assetRepository = mock(AssetRepository::class.java)
        assetService = AssetService(assetRepository)
    }
    
    @Test
    fun `test create asset`() = runBlocking {
        // Given
        val name = "Test Asset"
        val description = "This is a test asset"
        val acquisitionDate = LocalDate.now()
        val acquisitionCost = 1000.0
        val location = "Test Location"
        val category = AssetCategory.IT_EQUIPMENT
        
        whenever(assetRepository.save(any())).thenAnswer { it.arguments[0] as Asset }
        
        // When
        val result = assetService.createAsset(
            name = name,
            description = description,
            acquisitionDate = acquisitionDate,
            acquisitionCost = acquisitionCost,
            location = location,
            category = category
        )
        
        // Then
        assertNotNull(result)
        assertEquals(name, result.name)
        assertEquals(description, result.description)
        assertEquals(acquisitionDate, result.acquisitionDate)
        assertEquals(acquisitionCost, result.acquisitionCost)
        assertEquals(location, result.location)
        assertEquals(category, result.category)
        verify(assetRepository).save(any())
    }
    
    @Test
    fun `test get asset by id when exists`() = runBlocking {
        // Given
        val assetId = UUID.randomUUID()
        val asset = createTestAsset(assetId)
        whenever(assetRepository.findById(assetId)).thenReturn(asset)
        
        // When
        val result = assetService.getAssetById(assetId)
        
        // Then
        assertNotNull(result)
        assertEquals(assetId, result?.id)
        verify(assetRepository).findById(assetId)
    }
    
    @Test
    fun `test get asset by id when not exists`() = runBlocking {
        // Given
        val assetId = UUID.randomUUID()
        whenever(assetRepository.findById(assetId)).thenReturn(null)
        
        // When
        val result = assetService.getAssetById(assetId)
        
        // Then
        assertNull(result)
        verify(assetRepository).findById(assetId)
    }
    
    @Test
    fun `test add maintenance record`() = runBlocking {
        // Given
        val assetId = UUID.randomUUID()
        val asset = createTestAsset(assetId)
        val maintenanceRecord = MaintenanceRecord(
            date = LocalDate.now(),
            description = "Routine maintenance",
            cost = 100.0,
            performedBy = "Test Technician",
            maintenanceType = MaintenanceType.PREVENTIVE
        )
        
        whenever(assetRepository.findById(assetId)).thenReturn(asset)
        whenever(assetRepository.update(any())).thenAnswer { it.arguments[0] as Asset }
        
        // When
        val result = assetService.addMaintenanceRecord(assetId, maintenanceRecord)
        
        // Then
        assertNotNull(result)
        assertEquals(1, result?.maintenanceRecords?.size)
        assertEquals(maintenanceRecord.description, result?.maintenanceRecords?.get(0)?.description)
        assertEquals(AssetStatus.IN_MAINTENANCE, result?.status)
        verify(assetRepository).findById(assetId)
        verify(assetRepository).update(any())
    }
    
    private fun createTestAsset(id: UUID = UUID.randomUUID()): Asset {
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
