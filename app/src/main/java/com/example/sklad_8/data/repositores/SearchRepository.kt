package com.example.sklad_8.data.repositores

import com.example.sklad_8.data.db.SkladDatabase
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.fullTextSearch.Porter
import java.util.*

class SearchRepository(
    private val db: SkladDatabase
) {

    fun findGoodsByNameFullText(searchText: String) : List<GoodEntity> {
        val searchPorter = searchText.replace("\"(\\[\"]|.*)?\"".toRegex(), " ")
            .split(
                "[^\\p{Alpha}]+".toRegex()
            )
            .filter { it.isNotBlank() }
            .map(Porter::stem)
            .filter { it.length > 2 }
            .joinToString(
                separator = " ",
                transform = { "*${it[0].uppercaseChar()}${it.drop(1)}* OR *${it.lowercase(Locale.getDefault())}*" })

        return db.searchDao.searchByName(searchPorter)
    }

}