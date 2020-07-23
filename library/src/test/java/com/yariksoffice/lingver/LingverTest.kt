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
import android.content.res.Configuration
import com.yariksoffice.lingver.store.LocaleStore
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Locale

class LingverTest {

    private lateinit var lingver: Lingver

    private val store = mockk<LocaleStore>(relaxed = true)
    private val delegate = mockk<UpdateLocaleDelegate>(relaxed = true)
    private val context = mockk<Context>()
    private val application = mockk<Application>(relaxed = true)
    private val activity = mockk<Activity>(relaxed = true)
    private val configuration = mockk<Configuration>()
    private val appCallback = slot<LingverApplicationCallbacks>()
    private val activityCallback = slot<LingverActivityLifecycleCallbacks>()

    @Before
    fun setUp() {
        mockkStatic("com.yariksoffice.lingver.ExtensionsKt")
        lingver = Lingver.createInstance(store, delegate)
    }

    @Test
    fun setLocale_correctlyOverloadLocale() {
        val spy = spyk(lingver)

        spy.setLocale(context, TEST_LANGUAGE)
        verify { spy.setLocale(context, Locale(TEST_LANGUAGE)) }

        spy.setLocale(context, TEST_LANGUAGE, TEST_COUNTRY)
        verify { spy.setLocale(context, Locale(TEST_LANGUAGE, TEST_COUNTRY)) }

        spy.setLocale(context, TEST_LANGUAGE, TEST_COUNTRY, TEST_VARIANT)
        verify { spy.setLocale(context, Locale(TEST_LANGUAGE, TEST_COUNTRY, TEST_VARIANT)) }
    }

    @Test
    fun setLocale_persistCorrectLocale() {
        lingver.setLocale(context, TEST_LOCALE)

        verify { store.persistLocale(TEST_LOCALE) }
    }

    @Test
    fun setLocale_resetFollowingSystemLocale() {
        lingver.setLocale(context, TEST_LOCALE)

        verify { store.setFollowSystemLocale(false) }
    }

    @Test
    fun setLocale_callDelegateWithCorrectLocale() {
        lingver.setLocale(context, TEST_LOCALE)

        verify { delegate.applyLocale(context, TEST_LOCALE) }
    }

    @Test
    fun getLocale_returnCorrectLocale() {
        returnTestLocale()

        assertEquals(lingver.getLocale(), TEST_LOCALE)
    }

    @Test
    fun getLanguage_returnCorrectLanguage() {
        returnTestLocale()

        assertEquals(lingver.getLanguage(), TEST_LOCALE.language)
    }

    @Test
    fun getLanguage_replaceDeprecatedLanguageTags() {
        val pairs = listOf(Pair("iw", "he"), Pair("ji", "yi"), Pair("in", "id"))

        for (pair in pairs) {
            val locale = Locale(pair.first)

            every { store.getLocale() } returns locale
            assertEquals(lingver.getLanguage(), pair.second)
        }
    }

    @Test
    fun setFollowSystemLocale_persistSetting() {
        lingver.setFollowSystemLocale(context)

        verify { store.setFollowSystemLocale(true) }
    }

    @Test
    fun setFollowSystemLocale_persistSystemLocale() {
        lingver.systemLocale = SYSTEM_LOCALE

        lingver.setFollowSystemLocale(context)

        verify { store.persistLocale(SYSTEM_LOCALE) }
    }

    @Test
    fun setFollowSystemLocale_callDelegateWithSystemLocale() {
        lingver.systemLocale = SYSTEM_LOCALE

        lingver.setFollowSystemLocale(context)

        verify { delegate.applyLocale(context, SYSTEM_LOCALE) }
    }

    @Test
    fun isFollowingSystemLocale_returnCorrectSetting() {
        followSystemLocale()
        assertTrue(lingver.isFollowingSystemLocale())

        dontFollowSystemLocale()
        assertFalse(lingver.isFollowingSystemLocale())
    }

    @Test
    fun initialize_registerActivityAndComponentCallbacks() {
        lingver.initialize(application)

        verify { application.registerActivityLifecycleCallbacks(any()) }
        verify { application.registerComponentCallbacks(any()) }
    }

    @Test
    fun initialize_isFollowingSystemLocale_persistSystemLocale() {
        followSystemLocale()
        lingver.systemLocale = SYSTEM_LOCALE

        lingver.initialize(application)

        verify { store.persistLocale(SYSTEM_LOCALE) }
    }

    @Test
    fun initialize_isFollowingSystemLocale_applySystemLocale() {
        followSystemLocale()
        lingver.systemLocale = SYSTEM_LOCALE

        lingver.initialize(application)

        verify { delegate.applyLocale(application, SYSTEM_LOCALE) }
    }

    @Test
    fun initialize_isNotFollowingSystemLocale_persistLocale() {
        dontFollowSystemLocale()
        returnTestLocale()

        lingver.initialize(application)

        verify { store.persistLocale(TEST_LOCALE) }
    }

    @Test
    fun initialize_isNotFollowingSystemLocale_applyLocale() {
        dontFollowSystemLocale()
        returnTestLocale()

        lingver.initialize(application)

        verify { delegate.applyLocale(application, TEST_LOCALE) }
    }

    @Test
    fun configurationChange_isFollowingSystemLocale_persistSystemLocale() {
        prepareComponentCallback()
        followSystemLocale()

        appCallback.captured.onConfigurationChanged(configuration)

        verify { store.persistLocale(CONFIGURATION_CHANGE_LOCALE) }
    }

    @Test
    fun configurationChange_isFollowingSystemLocale_applySystemLocale() {
        prepareComponentCallback()
        followSystemLocale()

        appCallback.captured.onConfigurationChanged(configuration)

        verify { delegate.applyLocale(application, CONFIGURATION_CHANGE_LOCALE) }
    }

    @Test
    fun configurationChange_isFollowingSystemLocale_keepSystemLocaleInMemory() {
        lingver.systemLocale = SYSTEM_LOCALE
        prepareComponentCallback()

        appCallback.captured.onConfigurationChanged(configuration)

        assertEquals(lingver.systemLocale, CONFIGURATION_CHANGE_LOCALE)
    }

    @Test
    fun configurationChange_isNotFollowingSystemLocale_applyCurrentLocale() {
        prepareComponentCallback()
        dontFollowSystemLocale()
        returnTestLocale()

        appCallback.captured.onConfigurationChanged(configuration)

        verify { delegate.applyLocale(application, TEST_LOCALE) }
    }

    @Test
    fun configurationChange_isNotFollowingSystemLocale_dontPersistAnything() {
        prepareComponentCallback()
        dontFollowSystemLocale()

        appCallback.captured.onConfigurationChanged(configuration)

        verify(exactly = 0) { store.persistLocale(any()) }
    }

    @Test
    fun activityOnCreate_applyLocale() {
        prepareActivityCallback()
        returnTestLocale()

        activityCallback.captured.onActivityCreated(activity, null)

        verify { delegate.applyLocale(activity, TEST_LOCALE) }
    }

    @Test
    fun activityOnCreate_resetActivityTitle() {
        prepareActivityCallback()

        activityCallback.captured.onActivityCreated(activity, null)

        verify { activity.resetTitle() }
    }

    private fun prepareActivityCallback() {
        every {
            application.registerActivityLifecycleCallbacks(capture(activityCallback))
        } returns Unit

        lingver.initialize(application)
    }

    private fun prepareComponentCallback() {
        every { application.registerComponentCallbacks(capture(appCallback)) } returns Unit
        every { configuration.getLocaleCompat() } returns CONFIGURATION_CHANGE_LOCALE

        lingver.initialize(application)
        clearMocks(store)
    }

    private fun followSystemLocale() {
        every { store.isFollowingSystemLocale() } returns true
    }

    private fun dontFollowSystemLocale() {
        every { store.isFollowingSystemLocale() } returns false
    }

    private fun returnTestLocale() {
        every { store.getLocale() } returns TEST_LOCALE
    }

    companion object {
        private const val TEST_LANGUAGE = "en"
        private const val TEST_COUNTRY = "US"
        private const val TEST_VARIANT = "variant"
        private val TEST_LOCALE = Locale.US
        private val SYSTEM_LOCALE = Locale.CANADA
        private val CONFIGURATION_CHANGE_LOCALE = Locale.UK
    }
}
