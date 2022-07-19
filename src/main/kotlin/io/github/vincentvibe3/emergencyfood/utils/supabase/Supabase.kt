package io.github.vincentvibe3.emergencyfood.utils.supabase

import io.github.vincentvibe3.emergencyfood.internals.Config
import io.github.vincentvibe3.emergencyfood.utils.RequestHandler
import org.json.JSONArray
import org.json.JSONObject

object Supabase {

    private fun generateJson(data:HashMap<String, Any>):JSONObject{
        val result = JSONObject()
        data.forEach {
            result.put(it.key, it.value)
        }
        return result
    }

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
        return RequestHandler.get(endpoint, headers)
    }

    suspend fun update(tableName:String, row: HashMap<String, Any>, query:List<SupabaseFilter> = listOf()): String? {
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
        return RequestHandler.patch(endpoint, generateJson(row).toString(), headers)
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
        return RequestHandler.delete(endpoint, headers)
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
        return RequestHandler.post(endpoint, data, headers)
    }

    suspend fun upsert(tableName:String, row: HashMap<String, Any>): String {
        return insertImpl(tableName, generateJson(row).toString(), upsert = true)
    }

    suspend fun insert(tableName:String, row: HashMap<String, Any>): String {
        return insertImpl(tableName, generateJson(row).toString())
    }

    suspend fun insert(tableName:String, rows: List<HashMap<String, Any>>): String {
        val data = JSONArray()
        rows.forEach{
            data.put(generateJson(it))
        }
        return insertImpl(tableName, data.toString(), false)
    }

}