package ru.yandex.stockfly.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.yandex.stockfly.model.NewsItem


@Entity(
    tableName = "news",
    foreignKeys = [ForeignKey(
        entity = CompanyEntity::class,
        parentColumns = ["ticker"],
        childColumns = ["ticker"],
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )],
    indices = [Index("ticker"), Index("datetime")]
)
data class NewsItemEntity(
    @PrimaryKey
    val id: Long = 0,
    val ticker: String = "",
    val datetime: Long = 0,
    val headline: String = "",
    val imageUrl: String = "",
    val summary: String = "",
    val source: String = "",
    val url: String = "",
)


fun NewsItemEntity.toModel(): NewsItem {
    return NewsItem(id, datetime, headline, imageUrl, summary, source, url)
}


fun NewsItem.toEntity(ticker: String): NewsItemEntity {
    return NewsItemEntity(id, ticker, datetime, headline, imageUrl, summary, source, url)
}