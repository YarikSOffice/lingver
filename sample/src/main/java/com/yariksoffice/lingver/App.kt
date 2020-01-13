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

import android.app.Application
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import java.util.*

class App : Application() {

    @Suppress("UNUSED_VARIABLE")
    override fun onCreate() {
        super.onCreate()
        val store = PreferenceLocaleStore(this, Locale(LANGUAGE_ENGLISH))
        // you can use this instance for DI or get it via Lingver.getInstance() later on
        val lingver = Lingver.init(this, store)
    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_ENGLISH_COUNTRY = "US"
        const val LANGUAGE_UKRAINIAN = "uk"
        const val LANGUAGE_UKRAINIAN_COUNTRY = "UA"
        const val LANGUAGE_RUSSIAN = "ru"
        const val LANGUAGE_RUSSIAN_COUNTRY = "RU"
    }
}
