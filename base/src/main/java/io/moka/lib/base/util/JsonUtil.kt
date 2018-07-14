package io.moka.lib.base.util

import com.google.gson.Gson
import org.json.JSONArray

inline fun <reified T> String.string2Array(): ArrayList<T> {
    try {
        val jsonArray = JSONArray(this)
        val list = arrayListOf<T>()
        (0 until jsonArray.length()).forEach {
            list.add(jsonArray.get(it) as T)
        }
        return list
    } catch (e: Exception) {
        e.printStackTrace()
        return arrayListOf()
    }
}

fun <T> ArrayList<T>.array2String(): String {
    return Gson().toJson(this)
}

