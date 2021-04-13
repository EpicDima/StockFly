package ru.yandex.stockfly.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.yandex.stockfly.db.entity.CompanyEntity

@Dao
interface CompanyDao {

    @Query("SELECT * FROM companies WHERE ticker = :ticker")
    suspend fun select(ticker: String): CompanyEntity?

    @Query("SELECT * FROM companies")
    suspend fun selectAllAsList(): List<CompanyEntity>

    @Query("SELECT * FROM companies")
    fun selectAll(): LiveData<List<CompanyEntity>>

    @Query("SELECT * FROM companies WHERE favourite = 1")
    fun selectFavourites(): LiveData<List<CompanyEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(company: CompanyEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(companies: List<CompanyEntity>)

    @Update
    suspend fun update(company: CompanyEntity)

    @Update
    suspend fun update(companies: List<CompanyEntity>)

    @Query("UPDATE companies SET favourite = :favourite WHERE ticker = :ticker")
    suspend fun updateFavourite(ticker: String, favourite: Boolean): Int

    @Transaction
    suspend fun upsertAndSelect(company: CompanyEntity): CompanyEntity {
        if (insert(company) == -1L) {
            val favourite = select(company.ticker)!!.favourite
            update(company.copy(favourite = favourite))
        }
        return select(company.ticker)!!
    }
}