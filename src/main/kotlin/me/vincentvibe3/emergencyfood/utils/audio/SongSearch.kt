package me.vincentvibe3.emergencyfood.utils.audio


import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.success
import kotlinx.coroutines.GlobalScope
import me.vincentvibe3.emergencyfood.utils.RequestHandler
import me.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import me.vincentvibe3.emergencyfood.utils.exceptions.SongNotFoundException
import org.jsoup.Jsoup
import org.json.JSONObject

object SongSearch {

    //create a search query for YouTube
    private fun composeQueryLink(query: String):String {
        val newQuery = query.replace(" ", "+")
        return "https://www.youtube.com/results?search_query=$newQuery"
    }

    private fun parseSearchResults(rawPage:String):String{
        val doc = Jsoup.parse(rawPage)
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

                for ((index, _) in videoList.withIndex()){
                    if (videoList.getJSONObject(index).has("videoRenderer")){
                        return videoList.getJSONObject(index).getJSONObject("videoRenderer").getString("videoId")
                    }
                }
            }
        }
        return ""
    }

    //get the url to a song search on YouTube
    suspend fun getSong(query:String):String{
        lateinit var videoId:String
        var requestSuccess = true
        try{
            val result = RequestHandler.get(composeQueryLink(query))
            videoId = parseSearchResults(result)
        } catch (e:RequestFailedException){
            requestSuccess = false
        }
        if (videoId==""||!requestSuccess){
            throw SongNotFoundException()
        }
        return "https://www.youtube.com/watch?v=$videoId"
    }

}