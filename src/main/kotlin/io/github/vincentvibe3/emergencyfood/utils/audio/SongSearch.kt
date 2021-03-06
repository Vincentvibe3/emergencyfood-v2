package io.github.vincentvibe3.emergencyfood.utils.audio

import io.github.vincentvibe3.emergencyfood.utils.RequestHandler
import io.github.vincentvibe3.emergencyfood.utils.exceptions.RequestFailedException
import io.github.vincentvibe3.emergencyfood.utils.exceptions.SongNotFoundException
import org.json.JSONObject
import org.jsoup.Jsoup

object SongSearch {

    //create a search query for YouTube
    private fun composeQueryLink(query: String): String {
        val newQuery = query.replace(" ", "+")
        return "https://www.youtube.com/results?search_query=$newQuery"
    }

    private fun parseSearchResults(rawPage: String): String {
        val doc = Jsoup.parse(rawPage)
        doc.select("script").forEach { script ->
            if (script.data().startsWith("var ytInitialData = ")) {
                val json = JSONObject(script.data().removePrefix("var ytInitialData = "))
                val content = json.getJSONObject("contents")
                    .getJSONObject("twoColumnSearchResultsRenderer")
                    .getJSONObject("primaryContents")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents")

                for ((contentIndex, _) in content.withIndex()) {
                    val element = content.getJSONObject(contentIndex).getJSONObject("itemSectionRenderer")
                    if (element.has("contents")) {
                        val videoList = element.getJSONArray("contents")
                        for ((index, _) in videoList.withIndex()) {
                            if (videoList.getJSONObject(index).has("videoRenderer")) {
                                return videoList.getJSONObject(index).getJSONObject("videoRenderer")
                                    .getString("videoId")
                            }
                        }
                    }

                }
            }
        }
        return ""
    }

    //get the url to a song search on YouTube
    suspend fun getSong(query: String): String {
        lateinit var videoId: String
        var requestSuccess = true
        try {
            val result = RequestHandler.get(composeQueryLink(query))
            videoId = parseSearchResults(result)
        } catch (e: RequestFailedException) {
            requestSuccess = false
        }
        if (videoId == "" || !requestSuccess) {
            throw SongNotFoundException()
        }
        return "https://www.youtube.com/watch?v=$videoId"
    }

}