package com.example.sklad_8.data.network

import com.example.sklad_8.data.network.responses.InfoAtPackegs
import com.example.sklad_8.data.network.responses.SyncPackagesDescriptionsResponse
import com.example.sklad_8.data.network.responses.SyncPackagesResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(PACKAGES)
    suspend fun getPackages(
        @Query("uuid_package") uuid_package: String,
        @Query("pre_uuid_package") pre_uuid_package: String
    ): Response<SyncPackagesResponse>

    @GET(PACKAGES_DESCRIPTIONS)
    suspend fun getPackagesDescriptions(): Response<SyncPackagesDescriptionsResponse>

    companion object {
        const val PACKAGES = "/getPartGoods/"
        const val PACKAGES_DESCRIPTIONS = "/getDescriptionParts"
        const val PRODUCTS = "/SkladService/hs/ms/v1/products"
    }

    @GET(PRODUCTS)
    suspend fun getInfoAtPackegs(
        @Query("operation") operation: String,
    ): Response<InfoAtPackegs>

    @GET(PRODUCTS)
    suspend fun getPortion(
        @Query("getPortion") operation: String,
        @Query("portionsId") portionsId: String,
        @Query("numberPortion") numberPortion: String,
        @Query("dataType") dataType: String
    ): Response<InfoAtPackegs>

}