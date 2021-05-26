package com.example.sklad_8.data.mappers

import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.ui.goods.GoodViewData

fun List<GoodEntity>.toUi() : List<GoodViewData>{
    return this.map {
        GoodViewData(
            id = it.id,
            title = it.name,
            article = it.vendorCode,
            isGroup = it.isGroup,
            isHeader = false,
            parent = it.parent
        )
    }
}

fun GoodEntity.toUI(isHeader: Boolean = false): GoodViewData {
    return GoodViewData(
        id = this.id,
        title = this.name,
        article = this.vendorCode,
        isGroup = this.isGroup,
        isHeader = isHeader,
        parent = this.parent
    )
}