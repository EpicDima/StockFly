package com.epicdima.stockfly.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.epicdima.stockfly.db.entity.CompanyEntity

@Dao
interface CompanyDao {

    @Query("SELECT * FROM companies WHERE ticker = :ticker")
    suspend fun select(ticker: String): CompanyEntity?

    @Query("SELECT * FROM companies")
    suspend fun selectAllAsList(): List<CompanyEntity>

    @Query("SELECT * FROM companies")
    fun selectAll(): LiveData<List<CompanyEntity>>

    @Query("SELECT * FROM companies WHERE favourite = 1 ORDER BY favouriteNumber")
    suspend fun selectFavouritesAsList(): List<CompanyEntity>

    @Query("SELECT * FROM companies WHERE favourite = 1 ORDER BY favouriteNumber")
    fun selectFavourites(): LiveData<List<CompanyEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(company: CompanyEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(companies: List<CompanyEntity>)

    @Update
    suspend fun update(company: CompanyEntity)

    @Update
    suspend fun update(companies: List<CompanyEntity>)

    @Transaction
    suspend fun upsertAndSelect(company: CompanyEntity): CompanyEntity {
        if (insert(company) == -1L) {
            val existing = select(company.ticker)!!
            update(
                company.copy(
                    favourite = existing.favourite,
                    favouriteNumber = existing.favouriteNumber
                )
            )
        }
        return select(company.ticker)!!
    }
}
