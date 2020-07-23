/*
 * The MIT License (MIT)
 *
 * Copyright 2019 Yaroslav Berezanskyi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yariksoffice.lingver.store

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.util.Locale

/**
 * Default implementation of [LocaleStore] using [SharedPreferences].
 */
class PreferenceLocaleStore @JvmOverloads constructor(
    context: Context,
    private val defaultLocale: Locale = Locale.getDefault(),
    preferenceName: String = DEFAULT_PREFERENCE_NAME
) : LocaleStore {

    private val prefs = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    override fun getLocale(): Locale {
        // there is no predefined way to serialize/deserialize Locale object
        return if (!prefs.getString(LANGUAGE_KEY, null).isNullOrBlank()) {
            val json = JSONObject(prefs.getString(LANGUAGE_KEY, null)!!)
            val language = json.getString(LANGUAGE_JSON_KEY)
            val country = json.getString(COUNTRY_JSON_KEY)
            val variant = json.getString(VARIANT_JSON_KEY)
            Locale(language, country, variant)
        } else {
            defaultLocale
        }
    }

    override fun persistLocale(locale: Locale) {
        val json = JSONObject().apply {
            put(LANGUAGE_JSON_KEY, locale.language)
            put(COUNTRY_JSON_KEY, locale.country)
            put(VARIANT_JSON_KEY, locale.variant)
        }
        prefs.edit().putString(LANGUAGE_KEY, json.toString()).apply()
    }

    override fun setFollowSystemLocale(value: Boolean) {
        prefs.edit().putBoolean(FOLLOW_SYSTEM_LOCALE_KEY, value).apply()
    }

    override fun isFollowingSystemLocale(): Boolean {
        return prefs.getBoolean(FOLLOW_SYSTEM_LOCALE_KEY, false)
    }

    companion object {
        private const val LANGUAGE_KEY = "language_key"
        private const val FOLLOW_SYSTEM_LOCALE_KEY = "follow_system_locale_key"
        private const val DEFAULT_PREFERENCE_NAME = "lingver_preference"
        private const val LANGUAGE_JSON_KEY = "language"
        private const val COUNTRY_JSON_KEY = "country"
        private const val VARIANT_JSON_KEY = "variant"
    }
}
