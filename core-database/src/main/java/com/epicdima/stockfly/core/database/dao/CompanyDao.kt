package com.epicdima.stockfly.core.database.dao

import androidx.room.*
import com.epicdima.stockfly.core.database.entity.CompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {

    @Query("SELECT * FROM companies WHERE ticker = :ticker")
    suspend fun selectAsModel(ticker: String): CompanyEntity?

    @Query("SELECT * FROM companies WHERE ticker = :ticker")
    fun select(ticker: String): Flow<CompanyEntity?>

    @Query("SELECT * FROM companies")
    suspend fun selectAllAsList(): List<CompanyEntity>

    @Query("SELECT * FROM companies")
    fun selectAll(): Flow<List<CompanyEntity>>

    @Query("SELECT * FROM companies WHERE favourite = 1 ORDER BY favouriteNumber")
    suspend fun selectFavouritesAsList(): List<CompanyEntity>

    @Query("SELECT * FROM companies WHERE favourite = 1 ORDER BY favouriteNumber")
    fun selectFavourites(): Flow<List<CompanyEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(company: CompanyEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(companies: List<CompanyEntity>)

    @Update
    suspend fun update(company: CompanyEntity)

    @Update
    suspend fun update(companies: List<CompanyEntity>)

    @Delete
    suspend fun delete(company: CompanyEntity)

    @Transaction
    suspend fun upsertAndSelect(company: CompanyEntity): CompanyEntity {
        if (insert(company) == -1L) {
            val existing = selectAsModel(company.ticker)!!
            update(
                company.copy(
                    favourite = existing.favourite,
                    favouriteNumber = existing.favouriteNumber
                )
            )
        }
        return selectAsModel(company.ticker)!!
    }
}
