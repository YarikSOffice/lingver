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
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

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
    fun setLocale_resetFollowingDeviceLocale() {
        lingver.setLocale(context, TEST_LOCALE)

        verify { store.setFollowDeviceLocale(false) }
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
    fun setFollowDeviceLocale_persistSetting() {
        lingver.setFollowDeviceLocale(context)

        verify { store.setFollowDeviceLocale(true) }
    }

    @Test
    fun setFollowDeviceLocale_persistDeviceLocale() {
        lingver.deviceLocale = DEVICE_LOCALE

        lingver.setFollowDeviceLocale(context)

        verify { store.persistLocale(DEVICE_LOCALE) }
    }

    @Test
    fun setFollowDeviceLocale_callDelegateWithDeviceLocale() {
        lingver.deviceLocale = DEVICE_LOCALE

        lingver.setFollowDeviceLocale(context)

        verify { delegate.applyLocale(context, DEVICE_LOCALE) }
    }

    @Test
    fun isFollowingDeviceLocale_returnCorrectSetting() {
        followDeviceLocale()
        assertEquals(lingver.isFollowingDeviceLocale(), true)

        dontFollowDeviceLocale()
        assertEquals(lingver.isFollowingDeviceLocale(), false)
    }

    @Test
    fun setup_registerActivityAndComponentCallbacks() {
        lingver.initialize(application)

        verify { application.registerActivityLifecycleCallbacks(any()) }
        verify { application.registerComponentCallbacks(any()) }
    }

    @Test
    fun setup_isFollowingDeviceLocale_persistDeviceLocale() {
        followDeviceLocale()
        lingver.deviceLocale = DEVICE_LOCALE

        lingver.initialize(application)

        verify { store.persistLocale(DEVICE_LOCALE) }
    }

    @Test
    fun setup_isFollowingDeviceLocale_applyDeviceLocale() {
        followDeviceLocale()
        lingver.deviceLocale = DEVICE_LOCALE

        lingver.initialize(application)

        verify { delegate.applyLocale(application, DEVICE_LOCALE) }
    }

    @Test
    fun setup_isNotFollowingDeviceLocale_persistLocale() {
        dontFollowDeviceLocale()
        returnTestLocale()

        lingver.initialize(application)

        verify { store.persistLocale(TEST_LOCALE) }
    }

    @Test
    fun setup_isNotFollowingDeviceLocale_applyLocale() {
        dontFollowDeviceLocale()
        returnTestLocale()

        lingver.initialize(application)

        verify { delegate.applyLocale(application, TEST_LOCALE) }
    }

    @Test
    fun configurationChange_isFollowingDeviceLocale_persistDeviceLocale() {
        prepareComponentCallback()
        followDeviceLocale()

        appCallback.captured.onConfigurationChanged(configuration)

        verify { store.persistLocale(CONFIGURATION_CHANGE_LOCALE) }
    }

    @Test
    fun configurationChange_isFollowingDeviceLocale_applyDeviceLocale() {
        prepareComponentCallback()
        followDeviceLocale()

        appCallback.captured.onConfigurationChanged(configuration)

        verify { delegate.applyLocale(application, CONFIGURATION_CHANGE_LOCALE) }
    }

    @Test
    fun configurationChange_isFollowingDeviceLocale_keepDeviceLocaleInMemory() {
        lingver.deviceLocale = DEVICE_LOCALE
        prepareComponentCallback()

        appCallback.captured.onConfigurationChanged(configuration)

        assertEquals(lingver.deviceLocale, CONFIGURATION_CHANGE_LOCALE)
    }

    @Test
    fun configurationChange_isNotFollowingDeviceLocale_applyCurrentLocale() {
        prepareComponentCallback()
        dontFollowDeviceLocale()
        returnTestLocale()

        appCallback.captured.onConfigurationChanged(configuration)

        verify { delegate.applyLocale(application, TEST_LOCALE) }
    }

    @Test
    fun configurationChange_isNotFollowingDeviceLocale_dontPersistAnything() {
        prepareComponentCallback()
        dontFollowDeviceLocale()

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

    private fun followDeviceLocale() {
        every { store.isFollowingDeviceLocale() } returns true
    }

    private fun dontFollowDeviceLocale() {
        every { store.isFollowingDeviceLocale() } returns false
    }

    private fun returnTestLocale() {
        every { store.getLocale() } returns TEST_LOCALE
    }

    companion object {
        private const val TEST_LANGUAGE = "en"
        private const val TEST_COUNTRY = "US"
        private const val TEST_VARIANT = "variant"
        private val TEST_LOCALE = Locale.US
        private val DEVICE_LOCALE = Locale.CANADA
        private val CONFIGURATION_CHANGE_LOCALE = Locale.UK
    }
}