package io.github.vincentvibe3.emergencyfood.utils.supabase

import io.github.vincentvibe3.emergencyfood.internals.Config
import io.github.vincentvibe3.emergencyfood.utils.RequestHandler
import io.github.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import kotlinx.coroutines.delay

object Supabase {

//    private fun generateJson(data:HashMap<String, Any>):JSONObject{
//        val result = JSONObject()
//        data.forEach {
//            result.put(it.key, it.value)
//        }
//        return result
//    }

    private fun createQuery(query:List<SupabaseFilter>): String {
        return query.joinToString("&")
    }

    suspend fun select(tableName:String, query:List<SupabaseFilter> = listOf()): String {
        val endpoint =  if (query.isEmpty()){
            "${Config.supabaseUrl}/rest/v1/$tableName?select=*"
        } else {
            "${Config.supabaseUrl}/rest/v1/$tableName?${createQuery(query)}&select=*"
        }
        val headers = hashMapOf(
            "apikey" to Config.supabaseKey,
            "Authorization" to "Bearer ${Config.supabaseKey}",
        )
        var retries = 0
        lateinit var lastException:RequestFailedException
        for (i in 1..3){
            try {
                return RequestHandler.get(endpoint, headers)
            } catch (e:RequestFailedException){
                lastException = e
                retries++
            }
            delay(1000)
        }
        throw lastException
    }

    suspend fun update(tableName:String, row: String, query:List<SupabaseFilter> = listOf()): String? {
        if (query.isEmpty()){
            return null
        }
        val endpoint = "${Config.supabaseUrl}/rest/v1/$tableName?${createQuery(query)}&select=*"
        val headers = hashMapOf(
            "apikey" to Config.supabaseKey,
            "Authorization" to "Bearer ${Config.supabaseKey}",
            "Content-Type" to "application/json",
            "Prefer" to "return=representation"
        )
        var retries = 0
        lateinit var lastException:RequestFailedException
        for (i in 1..3){
            try {
                return RequestHandler.patch(endpoint, row, headers)
            } catch (e:RequestFailedException){
                lastException = e
                retries++
            }
            delay(1000)
        }
        throw lastException
    }

    suspend fun delete(tableName:String, query:List<SupabaseFilter> = listOf()): String {
        val endpoint =  if (query.isEmpty()){
            "${Config.supabaseUrl}/rest/v1/$tableName"
        } else {
            "${Config.supabaseUrl}/rest/v1/$tableName?${createQuery(query)}"
        }
        val headers = hashMapOf(
            "apikey" to Config.supabaseKey,
            "Authorization" to "Bearer ${Config.supabaseKey}",
        )
        var retries = 0
        lateinit var lastException:RequestFailedException
        for (i in 1..3){
            try {
                return RequestHandler.delete(endpoint, headers)
            } catch (e:RequestFailedException){
                lastException = e
                retries++
            }
            delay(1000)
        }
        throw lastException
    }

    private suspend fun insertImpl(tableName:String, data: String,  single:Boolean= true, upsert:Boolean = false): String {
        val endpoint = "${Config.supabaseUrl}/rest/v1/$tableName"
        val headers = hashMapOf(
            "apikey" to Config.supabaseKey,
            "Authorization" to "Bearer ${Config.supabaseKey}",
            "Content-Type" to "application/json"
        )
        if (single){
            headers["Prefer"] = "return=representation"
        }
        if (upsert){
            headers["Prefer"] = "resolution=merge-duplicates"
        }
        var retries = 0
        lateinit var lastException:RequestFailedException
        for (i in 1..3){
            try {
                RequestHandler.post(endpoint, data, headers)
            } catch (e:RequestFailedException){
                lastException = e
                retries++
            }
            delay(1000)
        }
        throw lastException
    }

    suspend fun upsert(tableName:String, row: String): String {
        return insertImpl(tableName, row, upsert = true)
    }

    suspend fun insert(tableName:String, row: String): String {
        return insertImpl(tableName, row)
    }

//    suspend fun insert(tableName:String, rows: List<HashMap<String, Any>>): String {
//        val data = JSONArray()
//        rows.forEach{
//            data.put(generateJson(it))
//        }
//        return insertImpl(tableName, data.toString(), false)
//    }

}