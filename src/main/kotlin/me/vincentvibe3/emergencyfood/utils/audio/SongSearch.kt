package me.vincentvibe3.emergencyfood.utils.audio


import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.flow.asFlow
import org.jsoup.Jsoup
import org.json.JSONObject
import java.io.File

object SongSearch {

    private fun composeQueryLink(query: String):String {
        val newQuery = query.replace(" ", "+")
        return "https://www.youtube.com/results?search_query=$newQuery"
    }

    fun getSong(query:String):String{
        lateinit var videoId:String
        val httpAsync = composeQueryLink(query)
            .httpGet()
            .responseString { request, response, result ->
                val (bytes, error) = result
                if (bytes != null && response.statusCode == 200){
                    val doc = Jsoup.parse(bytes)
                    doc.select("script").forEach { script ->
                        if (script.data().startsWith("var ytInitialData = ")) {
                            val json = JSONObject(script.data().removePrefix("var ytInitialData = "))

                            val videoList = json.getJSONObject("contents")
                                .getJSONObject("twoColumnSearchResultsRenderer")
                                .getJSONObject("primaryContents")
                                .getJSONObject("sectionListRenderer")
                                .getJSONArray("contents")
                                .getJSONObject(0)
                                .getJSONObject("itemSectionRenderer")
                                .getJSONArray("contents")

                            for ((index, value) in videoList.withIndex()){
                                if (videoList.getJSONObject(index).has("videoRenderer")){
                                    videoId = videoList.getJSONObject(index).getJSONObject("videoRenderer").getString("videoId")
                                    break
                                }
                            }
                        }
                    }
                }
            }

        httpAsync.join()
        return "https://www.youtube.com/watch?v=$videoId"
    }

}