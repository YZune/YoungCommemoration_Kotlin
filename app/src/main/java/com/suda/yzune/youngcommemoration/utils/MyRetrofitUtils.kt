package com.suda.yzune.youngcommemoration.utils

import retrofit2.Retrofit

class MyRetrofitUtils private constructor() {
    private val retrofit = Retrofit.Builder().baseUrl("http://106.15.202.52:8080").build()
    private val myService = retrofit.create(MyRetrofitService::class.java)

    fun getService(): MyRetrofitService {
        return myService
    }

    companion object {
        val instance: MyRetrofitUtils by lazy { MyRetrofitUtils() }
    }
}