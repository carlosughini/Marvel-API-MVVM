package com.carlosughini.appmarvel.api

import com.carlosughini.appmarvel.model.Model
import com.google.gson.GsonBuilder
import com.squareup.okhttp.logging.HttpLoggingInterceptor
import com.squareup.okhttp.OkHttpClient
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.GET
import retrofit.http.Path
import retrofit.http.Query


interface MarvelApi {

    @GET("characters")
    public fun getCharacters(@Query("ts") ts: String,
                             @Query("apikey") apiKey: String,
                             @Query("hash") hash: String,
                             @Query("limit") limit: Int)
            : rx.Observable<Model.CharacterResponse>

    @GET("characters/{id}")
    public fun getCharacterInfo(@Path("id") id: String,
                                @Query("ts") ts: String,
                                @Query("apikey") apiKey: String,
                                @Query("hash") hash: String)
            : rx.Observable<Model.CharacterResponse>

    @GET("{type}/{id}")
    public fun getDetail(@Path("type") type: String,
                         @Path("id") id: String,
                         @Query("ts") ts: String,
                         @Query("apikey") apiKey: String,
                         @Query("hash") hash: String)
            : rx.Observable<Model.DetailResponse>

    companion object {
        val BASE_URL = "http://gateway.marvel.com/v1/public/"

        fun create() : MarvelApi {
            val gson = GsonBuilder()

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client: OkHttpClient = OkHttpClient()
            client.interceptors().add(loggingInterceptor)

            val retrofitAdapter = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson.create()))
                    .client(client)
                    .build()

            return retrofitAdapter.create(MarvelApi::class.java)
        }
    }
}