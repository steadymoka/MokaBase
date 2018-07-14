package io.moka.lib.base.preference

import android.content.Context
import android.content.SharedPreferences
import io.moka.lib.base.MokaBase
import io.moka.lib.base.util.array2String
import io.moka.lib.base.util.string2Array

open class SharedPreferenceManager(private val SHARED_PREFERENCE_NAME: String) {

    fun getEditor(context: Context): SharedPreferences.Editor {
        return getSharedPreferences(context).edit()
    }

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun clearPreference(context: Context) {
        getEditor(context).clear().commit()
    }

    /**


    Setter
     */

    fun setExtraLong(KEY: String, value: Long) = getEditor(MokaBase.context!!).putLong(KEY, value).commit()

    fun setExtraInt(KEY: String, value: Int) = getEditor(MokaBase.context!!).putInt(KEY, value).commit()

    fun setExtraFloat(KEY: String, value: Float) = getEditor(MokaBase.context!!).putFloat(KEY, value).commit()

    fun setExtraString(KEY: String, value: String) = getEditor(MokaBase.context!!).putString(KEY, value).commit()

    fun setExtraBoolean(KEY: String, value: Boolean) = getEditor(MokaBase.context!!).putBoolean(KEY, value).commit()

    fun <T> setExtraArrayList(KEY: String, value: ArrayList<T>) = getEditor(MokaBase.context!!).putString(KEY, value.array2String()).commit()

    /**


    Getter
     */

    fun getExtraLong(KEY: String, defaultValue: Long): Long = getSharedPreferences(MokaBase.context!!).getLong(KEY, defaultValue)

    fun getExtraInt(KEY: String, defaultValue: Int): Int = getSharedPreferences(MokaBase.context!!).getInt(KEY, defaultValue)

    fun getExtraFloat(KEY: String, defaultValue: Float): Float = getSharedPreferences(MokaBase.context!!).getFloat(KEY, defaultValue)

    fun getExtraString(KEY: String, defaultValue: String): String = getSharedPreferences(MokaBase.context!!).getString(KEY, defaultValue)

    fun getExtraBoolean(KEY: String, defaultValue: Boolean): Boolean = getSharedPreferences(MokaBase.context!!).getBoolean(KEY, defaultValue)

    inline fun <reified T> getExtraArrayList(KEY: String, defaultValue: ArrayList<T>) = getSharedPreferences(MokaBase.context!!).getString(KEY, defaultValue.array2String()).string2Array<T>()

}
