package com.heysoypaez.notiongtd.services

import com.heysoypaez.notiongtd.BuildConfig
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

import org.json.JSONArray
import org.json.JSONObject


object NotionAPI {
    const val BASE_URL = "https://api.notion.com/v1"
    const val VERSION = "2022-06-28"
    const val CONTENT_TYPE = "application/json; charset=utf-8"
}

object NotionGTD {
    const val PAGE = "3152c6b568104b818085b0f15d973ed0"
    const val INBOX = "2d36d2643dde46cb8a7f1870d79119e3"
}

class NotionService {

    private fun getBaseHeaders(): Headers {

        return Headers.Builder()
            .add("Authorization", "Bearer ${BuildConfig.API_TOKEN}")
            .add("Content-Type", NotionAPI.CONTENT_TYPE)
            .add("Notion-Version", NotionAPI.VERSION)
            .build()
    }

    fun retrieveNotionBlock(): Request {
        return Request.Builder().url("${NotionAPI.BASE_URL}/pages/${NotionGTD.PAGE}")
            .headers(getBaseHeaders())
            .get()
            .build()
    }

    fun updateNotionBlock(element: String): Request {

        val newBlock = JSONObject().apply {
            put("type", "text")
            put("text", JSONObject().apply {
                put("content", element)
            })
        }

        val richTextArray = JSONArray().apply { put(newBlock) }

        val requestData = JSONObject().put("children", JSONArray().apply {
            put(JSONObject().apply {
                put("object", "block")
                put("type", "numbered_list_item")
                put("numbered_list_item", JSONObject().apply {
                    put("rich_text", richTextArray)
                })
            })
        })

        val requestBody = requestData.toString()
            .toRequestBody(NotionAPI.CONTENT_TYPE.toMediaTypeOrNull())

        return Request.Builder()
            .url("${NotionAPI.BASE_URL}/blocks/${NotionGTD.INBOX}/children")
            .headers(getBaseHeaders())
            .patch(requestBody)
            .build()
    }

}
