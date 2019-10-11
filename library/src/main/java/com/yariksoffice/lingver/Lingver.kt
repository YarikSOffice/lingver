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

package com.yariksoffice.lingver

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager.GET_META_DATA
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Build.VERSION_CODES
import com.yariksoffice.lingver.store.LocaleStore
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import java.util.*

/**
 * Lingver is a tool to manage your application locale and language.
 *
 * Once you set a desired locale using [setLocale] method, Lingver will enforce your application
 * to provide correctly localized data via [Resources] class.
 */
class Lingver private constructor(private val store: LocaleStore) {

    /**
     * Sets the locale which will be used to localize all data coming from [Resources] class.
     *
     * <p>Note that you need to update all already fetched locale-based data manually.
     * [Lingver] is not responsible for that.
     *
     * <p>The language value is a two or three-letter language code as defined in ISO639.
     * See the [Locale] class description for more information
     * about valid language values.
     */
    fun setLocale(context: Context, language: String) {
        store.persistLocale(language)
        update(context, language)
    }

    fun getLocale(): String {
        return store.getLocal()
    }

    private fun setUp(application: Application) {
        application.registerActivityLifecycleCallbacks(LingverActivityLifecycleCallbacks(this))
        application.registerComponentCallbacks(LingverApplicationCallbacks(application, this))
    }

    internal fun setLocaleInternal(context: Context) {
        update(context, store.getLocal())
    }

    private fun update(context: Context, language: String) {
        updateResources(context, language)
        val appContext = context.applicationContext
        if (appContext !== context) {
            updateResources(appContext, language)
        }
    }

    @Suppress("DEPRECATION")
    private fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val res = context.resources
        if (alreadyDesiredLocale(language, res)) return

        val config = Configuration(res.configuration)
        if (isAtLeastSdkVersion(VERSION_CODES.JELLY_BEAN_MR1)) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        res.updateConfiguration(config, res.displayMetrics)
    }

    internal fun resetActivityTitle(activity: Activity) {
        try {
            val pm = activity.packageManager
            val info = pm.getActivityInfo(activity.componentName, GET_META_DATA)
            if (info.labelRes != 0) {
                activity.setTitle(info.labelRes)
            }
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun alreadyDesiredLocale(language: String, res: Resources): Boolean {
        // https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html#getLanguage--
        val desired = Locale(language).language
        val current = res.configuration.getLocaleCompat().language
        return current.equals(desired, ignoreCase = true)
    }

    @Suppress("DEPRECATION")
    private fun Configuration.getLocaleCompat(): Locale {
        return if (isAtLeastSdkVersion(VERSION_CODES.N)) locales.get(0) else locale
    }

    private fun isAtLeastSdkVersion(versionCode: Int): Boolean {
        return Build.VERSION.SDK_INT >= versionCode
    }

    companion object {

        private lateinit var instance: Lingver

        /**
         * Returns the global instance of [Lingver] created via init method.
         *
         * @throws IllegalStateException if it was not initialized properly.
         */
        @JvmStatic
        fun getInstance(): Lingver {
            check(::instance.isInitialized) { "Lingver should be initialized first" }
            return instance
        }

        @JvmStatic
        fun init(application: Application, defaultLanguage: String): Lingver {
            return init(application, PreferenceLocaleStore(application, defaultLanguage))
        }

        /**
         * Creates and sets up the global instance.
         *
         * This method must be called before any calls to [Lingver] and may only be called once.
         */
        @JvmStatic
        fun init(application: Application, store: LocaleStore): Lingver {
            check(!::instance.isInitialized) { "Already initialized" }
            val lingver = Lingver(store)
            lingver.setUp(application)
            lingver.setLocaleInternal(application)
            instance = lingver
            return lingver
        }
    }
}
