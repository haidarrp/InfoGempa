package org.d3if0130.infogempa.api

import org.d3if0130.infogempa.model.DataGempa
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET

interface ApiInstance {

    @GET("/gempadirasakan.xml")
    fun getData(): Call<DataGempa.Infogempa>

    companion object {
        fun create(): ApiInstance {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl("http://data.bmkg.go.id")
                .build()
            return retrofit.create(ApiInstance::class.java)
        }
    }
}