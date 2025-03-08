package solutions.dreamforge.infrastructure.repository

import jakarta.enterprise.context.ApplicationScoped
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solutions.dreamforge.domain.model.Asset
import solutions.dreamforge.domain.model.AssetCategory
import solutions.dreamforge.domain.model.AssetStatus
import solutions.dreamforge.domain.repository.AssetRepository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation of the AssetRepository.
 * This class provides a non-persistent storage solution for assets,
 * primarily intended for development, testing, and prototyping.
 *
 * Key implementation details:
 * - Uses ConcurrentHashMap for thread-safe storage
 * - Uses Mutex for additional synchronization of complex operations
 * - Implements all methods defined in the AssetRepository interface
 *
 * In a production environment, this would typically be replaced with
 * a database-backed implementation (e.g., using Hibernate/JPA).
 *
 * The @ApplicationScoped annotation uses Quarkus Arc's CDI implementation to ensure 
 * a single instance is shared across the application, following the Singleton pattern.
 * Arc provides optimized bean instantiation and lifecycle management.
 */
@ApplicationScoped
class InMemoryAssetRepository : AssetRepository {
    /**
     * Thread-safe storage for Asset instances, keyed by their UUID.
     */
    private val assets = ConcurrentHashMap<UUID, Asset>()
    
    /**
     * Mutex for synchronizing complex operations.
     * While ConcurrentHashMap provides thread-safety for individual operations,
     * the Mutex ensures atomic execution of multi-step operations.
     */
    private val mutex = Mutex()

    /**
     * {@inheritDoc}
     */
    override suspend fun findById(id: UUID): Asset? {
        return assets[id]
    }

    /**
     * {@inheritDoc}
     */
    override suspend fun findAll(): List<Asset> {
        return assets.values.toList()
    }

    /**
     * {@inheritDoc}
     */
    override suspend fun findByStatus(status: AssetStatus): List<Asset> {
        return assets.values.filter { it.status == status }
    }

    /**
     * {@inheritDoc}
     */
    override suspend fun findByCategory(category: AssetCategory): List<Asset> {
        return assets.values.filter { it.category == category }
    }

    /**
     * {@inheritDoc}
     */
    override suspend fun findByLocation(location: String): List<Asset> {
        return assets.values.filter { it.location == location }
    }

    /**
     * {@inheritDoc}
     * 
     * Uses Mutex to ensure the asset is added atomically, even in concurrent scenarios.
     */
    override suspend fun save(asset: Asset): Asset {
        mutex.withLock {
            assets[asset.id] = asset
        }
        return asset
    }

    /**
     * {@inheritDoc}
     * 
     * Uses Mutex to ensure the update operation is atomic.
     * Throws IllegalArgumentException if the asset with the given ID doesn't exist.
     */
    override suspend fun update(asset: Asset): Asset {
        mutex.withLock {
            if (!assets.containsKey(asset.id)) {
                throw IllegalArgumentException("Asset with ID ${asset.id} not found")
            }
            assets[asset.id] = asset
        }
        return asset
    }

    /**
     * {@inheritDoc}
     * 
     * Uses Mutex to ensure the delete operation is atomic.
     */
    override suspend fun delete(id: UUID): Boolean {
        mutex.withLock {
            return assets.remove(id) != null
        }
    }
}
