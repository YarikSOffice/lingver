# Lingver

[![](https://jitpack.io/v/YarikSOffice/lingver.svg)](https://jitpack.io/#YarikSOffice/lingver)

Lingver is a library to manage your application locale and language.
 
Once you set a desired locale, Lingver will enforce your application to provide correctly localized data via Resources class.

The library contains an implementation of the approach described in the following [blogpost](https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758).

## Setup

The setup is pretty simple:

1. Initialize the library in Application.onCreate:

``` kotlin
Lingver.init(this, defaultLanguage)
```
See the sample app for more customization options.

2. Change a locale, for instance, from your setting screen:

``` kotlin
 Lingver.getInstance().setLocale(this, language)
```

Note that you need to update all already fetched locale-based data manually. Lingver is not responsible for that.

## Download

``` groovy
repositories {
	maven { url 'https://jitpack.io' }
}

dependencies {
	implementation "com.github.YarikSOffice:lingver:1.1.0"
}
```

## License

```
The MIT License (MIT)

Copyright 2019 Yaroslav Berezanskyi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
