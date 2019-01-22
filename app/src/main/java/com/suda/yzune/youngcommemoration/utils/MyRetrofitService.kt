package com.suda.yzune.youngcommemoration.utils

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface MyRetrofitService {

    @GET("/school/getupdate_mie")
    fun getUpdateInfo(): Call<ResponseBody>

}