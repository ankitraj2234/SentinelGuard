package com.sentinelguard.data.database.dao

import androidx.room.*
import com.sentinelguard.data.database.entities.CellTowerCacheEntity
import com.sentinelguard.data.database.entities.CellTowerHistoryEntity
import com.sentinelguard.data.database.entities.CellTowerIncidentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CellTowerDao {
    
    // ==================== Cache Operations ====================
    
    @Query("""
        SELECT * FROM cell_tower_cache 
        WHERE cellId = :cellId AND lac = :lac AND mcc = :mcc AND mnc = :mnc 
        AND expiresAt > :currentTime
        LIMIT 1
    """)
    suspend fun getCachedTower(
        cellId: String, 
        lac: String, 
        mcc: String, 
        mnc: String, 
        currentTime: Long
    ): CellTowerCacheEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCache(tower: CellTowerCacheEntity)
    
    @Query("DELETE FROM cell_tower_cache WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredCache(currentTime: Long)
    
    // ==================== History Operations ====================
    
    @Insert
    suspend fun insertHistory(history: CellTowerHistoryEntity): Long
    
    @Update
    suspend fun updateHistory(history: CellTowerHistoryEntity)
    
    @Query("SELECT * FROM cell_tower_history WHERE disconnectedAt IS NULL ORDER BY connectedAt DESC LIMIT 1")
    suspend fun getCurrentConnection(): CellTowerHistoryEntity?
    
    @Query("SELECT * FROM cell_tower_history ORDER BY connectedAt DESC LIMIT :limit")
    suspend fun getRecentHistory(limit: Int): List<CellTowerHistoryEntity>
    
    @Query("SELECT * FROM cell_tower_history ORDER BY connectedAt DESC LIMIT 1")
    suspend fun getLastHistoryEntry(): CellTowerHistoryEntity?
    
    @Query("SELECT * FROM cell_tower_history WHERE connectedAt >= :startTime ORDER BY connectedAt ASC")
    suspend fun getHistorySince(startTime: Long): List<CellTowerHistoryEntity>
    
    @Query("SELECT * FROM cell_tower_history ORDER BY connectedAt DESC")
    fun observeHistory(): Flow<List<CellTowerHistoryEntity>>
    
    @Query("""
        UPDATE cell_tower_history 
        SET disconnectedAt = :disconnectedAt 
        WHERE disconnectedAt IS NULL
    """)
    suspend fun closeOpenConnections(disconnectedAt: Long)
    
    @Query("DELETE FROM cell_tower_history")
    suspend fun clearAllHistory()
    
    @Query("SELECT COUNT(DISTINCT cellId) FROM cell_tower_history WHERE connectedAt >= :startTime")
    suspend fun getUniqueTowerCount(startTime: Long): Int
    
    // ==================== Incident Operations ====================
    
    @Insert
    suspend fun insertIncident(incident: CellTowerIncidentEntity): Long
    
    @Update
    suspend fun updateIncident(incident: CellTowerIncidentEntity)
    
    @Query("SELECT * FROM cell_tower_incidents WHERE wasResolved = 0 ORDER BY occurredAt DESC")
    suspend fun getUnresolvedIncidents(): List<CellTowerIncidentEntity>
    
    @Query("SELECT * FROM cell_tower_incidents ORDER BY occurredAt DESC LIMIT :limit")
    suspend fun getRecentIncidents(limit: Int): List<CellTowerIncidentEntity>
    
    @Query("SELECT * FROM cell_tower_incidents WHERE wasEmailSent = 0")
    suspend fun getPendingEmailIncidents(): List<CellTowerIncidentEntity>
    
    @Query("UPDATE cell_tower_incidents SET wasEmailSent = 1 WHERE id = :incidentId")
    suspend fun markEmailSent(incidentId: Long)
    
    @Query("""
        SELECT * FROM cell_tower_incidents 
        WHERE cellId = :cellId AND occurredAt >= :sinceTime 
        ORDER BY occurredAt DESC
    """)
    suspend fun getIncidentsForTower(cellId: String, sinceTime: Long): List<CellTowerIncidentEntity>
    
    @Query("SELECT COUNT(*) FROM cell_tower_incidents WHERE riskLevel = 'HIGH' OR riskLevel = 'CRITICAL'")
    suspend fun getHighRiskIncidentCount(): Int
}
