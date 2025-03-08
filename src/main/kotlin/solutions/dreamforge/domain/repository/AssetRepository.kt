package solutions.dreamforge.domain.repository

import solutions.dreamforge.domain.model.Asset
import solutions.dreamforge.domain.model.AssetCategory
import solutions.dreamforge.domain.model.AssetStatus
import java.util.UUID

/**
 * Repository interface for Asset domain objects.
 * Following the Repository pattern from Domain-Driven Design.
 *
 * This interface defines the contract for data access operations related to Assets.
 * It abstracts the underlying data store implementation, allowing the domain model
 * to remain persistence-agnostic.
 *
 * The methods are designed to be suspension functions to support asynchronous
 * operations, which is particularly important for reactive database access.
 *
 * Implementation classes of this interface might use different technologies:
 * - In-memory data structures (for testing and development)
 * - Relational databases via JPA/Hibernate
 * - NoSQL databases
 * - External APIs
 */
interface AssetRepository {
    /**
     * Finds an Asset by its unique identifier.
     *
     * @param id The UUID of the asset to find
     * @return The Asset if found, or null if not found
     */
    suspend fun findById(id: UUID): Asset?
    
    /**
     * Retrieves all Assets in the system.
     *
     * @return A list of all Assets, or an empty list if none exist
     */
    suspend fun findAll(): List<Asset>
    
    /**
     * Finds all Assets with a specific operational status.
     *
     * @param status The status to filter by
     * @return A list of Assets with the specified status, or an empty list if none exist
     */
    suspend fun findByStatus(status: AssetStatus): List<Asset>
    
    /**
     * Finds all Assets within a specific category.
     *
     * @param category The category to filter by
     * @return A list of Assets with the specified category, or an empty list if none exist
     */
    suspend fun findByCategory(category: AssetCategory): List<Asset>
    
    /**
     * Finds all Assets at a specific location.
     *
     * @param location The location to filter by
     * @return A list of Assets at the specified location, or an empty list if none exist
     */
    suspend fun findByLocation(location: String): List<Asset>
    
    /**
     * Persists a new Asset to the repository.
     *
     * @param asset The Asset to save
     * @return The saved Asset, potentially with generated IDs or other modifications
     */
    suspend fun save(asset: Asset): Asset
    
    /**
     * Updates an existing Asset in the repository.
     *
     * @param asset The Asset to update
     * @return The updated Asset
     * @throws IllegalArgumentException if the Asset doesn't exist in the repository
     */
    suspend fun update(asset: Asset): Asset
    
    /**
     * Removes an Asset from the repository.
     *
     * @param id The UUID of the Asset to delete
     * @return true if the Asset was successfully deleted, false if it wasn't found
     */
    suspend fun delete(id: UUID): Boolean
}
