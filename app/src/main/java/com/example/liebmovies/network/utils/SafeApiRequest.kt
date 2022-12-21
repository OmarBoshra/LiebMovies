package com.example.liebmovies.network.utils

import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

abstract class SafeApiRequest {

    suspend fun <T : Any> safeApiRequest(call: suspend () -> Response<T>): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val responseError = response.errorBody()?.string()
            val message = StringBuilder()
            responseError.let {
                try {
                    message.append(it?.let { it1 -> JSONObject(it1) })
                } catch (e: JSONException) {
                    Log.d("responseError", "safeApiRequest: $e")
                }
            }
            Log.d("responseError", "safeApiRequest: $message")
            val statusCode = response.code()
            throw ApiException(Status(statusCode, message.toString()))
        }
    }
}