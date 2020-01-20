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

import android.content.Context
import android.webkit.WebView
import androidx.annotation.MainThread

/**
 * WebViewLocaleHelper implements a workaround that fixes the unwanted side effect while
 * using a [WebView] introduced in Android N.
 *
 * For unknown reasons, the very first creation of a [WebView] (either programmatically
 * or via inflation) resets an application locale to the device default.
 * More on that: https://issuetracker.google.com/issues/37113860
 */
class WebViewLocaleHelper(private val context: Context) {

    private var requireWorkaround = true

    @MainThread
    fun implementWorkaround() {
        if (requireWorkaround) {
            requireWorkaround = false
            WebView(context).destroy()
            val lingver = Lingver.getInstance()
            lingver.setLocale(context, lingver.getLocale())
        }
    }
}