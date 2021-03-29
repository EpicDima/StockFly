package ru.yandex.stockfly.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.yandex.stockfly.db.entity.CompanyEntity

@Dao
interface CompanyDao {

    @Query("SELECT * FROM companies WHERE ticker = :ticker")
    suspend fun select(ticker: String): CompanyEntity?

    @Query("SELECT * FROM companies")
    fun selectAll(): LiveData<List<CompanyEntity>>

    @Query("SELECT * FROM companies WHERE favourite = 1")
    fun selectFavourites(): LiveData<List<CompanyEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(company: CompanyEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(companies: List<CompanyEntity>)

    @Query("UPDATE companies SET favourite = :favourite WHERE ticker = :ticker")
    suspend fun updateFavourite(ticker: String, favourite: Boolean): Int
}