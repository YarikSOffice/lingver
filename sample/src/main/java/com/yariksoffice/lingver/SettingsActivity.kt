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

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.yariksoffice.lingver.App.Companion.LANGUAGE_ENGLISH
import com.yariksoffice.lingver.App.Companion.LANGUAGE_ENGLISH_COUNTRY
import com.yariksoffice.lingver.App.Companion.LANGUAGE_RUSSIAN
import com.yariksoffice.lingver.App.Companion.LANGUAGE_RUSSIAN_COUNTRY
import com.yariksoffice.lingver.App.Companion.LANGUAGE_UKRAINIAN
import com.yariksoffice.lingver.App.Companion.LANGUAGE_UKRAINIAN_COUNTRY

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        findViewById<View>(R.id.en).setOnClickListener {
            setNewLocale(LANGUAGE_ENGLISH, LANGUAGE_ENGLISH_COUNTRY)
        }
        findViewById<View>(R.id.ua).setOnClickListener {
            setNewLocale(LANGUAGE_UKRAINIAN, LANGUAGE_UKRAINIAN_COUNTRY)
        }
        findViewById<View>(R.id.ru).setOnClickListener {
            setNewLocale(LANGUAGE_RUSSIAN, LANGUAGE_RUSSIAN_COUNTRY)
        }
        findViewById<View>(R.id.device_locale).setOnClickListener {
            followDeviceLocale()
        }
    }

    private fun setNewLocale(language: String, country: String) {
        Lingver.getInstance().setLocale(this, language, country)
        restart()
    }

    private fun followDeviceLocale() {
        Lingver.getInstance().setFollowDeviceLocale(this)
        restart()
    }

    private fun restart() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

        Toast.makeText(this, "Activity restarted", Toast.LENGTH_SHORT).show()
    }
}