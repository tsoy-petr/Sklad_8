package com.example.sklad_8.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "img_good",
    foreignKeys = [ForeignKey(
        entity = GoodEntity::class,
        parentColumns = ["id"],
        childColumns = ["goodId"],
        onDelete = CASCADE
    )]
)
data class ImgGoodEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(index = true)
    val goodId: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val imgDigit: ByteArray? = null,
    val isMain: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImgGoodEntity

        if (id != other.id) return false
        if (goodId != other.goodId) return false
        if (imgDigit != null) {
            if (other.imgDigit == null) return false
            if (!imgDigit.contentEquals(other.imgDigit)) return false
        } else if (other.imgDigit != null) return false
        if (isMain != other.isMain) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + goodId.hashCode()
        result = 31 * result + (imgDigit?.contentHashCode() ?: 0)
        result = 31 * result + isMain.hashCode()
        return result
    }

}
