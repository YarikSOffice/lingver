# Lingver

[![](https://jitpack.io/v/YarikSOffice/lingver.svg)](https://jitpack.io/#YarikSOffice/lingver)

Lingver is a library to manage your application locale and language.
 
Once you set a desired locale, Lingver will enforce your application to provide correctly localized data via Resources class.

The library contains an implementation of the approach described in the following [blogpost](https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758).

## Setup

The setup is pretty simple:

1. Initialize the library in Application.onCreate:

``` kotlin
Lingver.init(context, defaultLanguage)
```
See the sample app for more customization options.

2. Change a locale, for instance, from your setting screen:

``` kotlin
 Lingver.getInstance().setLocale(context, language)
```

Note that you need to update all already fetched locale-based data manually. Lingver is not responsible for that.

## Follow the device locale

You can configure Lingver to follow the device locale whenever it changes:

 ``` kotlin
  Lingver.getInstance().setFollowDeviceLocale(context)
 ```

Note that any call to `setLocale()` stops following the device locale and resets `isFollowingDeviceLocale()` setting. 

## WebView

Starting from Android N, there is a weird [side effect](https://issuetracker.google.com/issues/37113860) while using a [WebView](https://developer.android.com/reference/android/webkit/WebView)
in your application. For unknown reasons, the very first creation of it (either programmatically or via inflation)
resets an application locale to the device default. Obviously, this is not what we expect to happen.
Moreover, it's not going to be fixed anytime in the future according to the [issuetracker](https://issuetracker.google.com/issues/37113860).
That's why we should somehow deal with it on our own.

There are plenty of ways how we can fix that, but the idea stays always the same. You have to set back 
the desired locale after the first usage of a WebView. For instance, you can even programmatically create
a fake WebView and immediately set a locale back which prevents this side effect from happening in the future.
See an example of implementation in the sample app.

## App Bundles

While using an [app bundle](https://developer.android.com/guide/app-bundle), a user’s device only downloads string resources
that match the one or more languages currently selected in the device’s settings. Refer to [this page](https://stackoverflow.com/questions/52731670/android-app-bundle-with-in-app-locale-change) if you want to change this behavior and have access to additional language resources.

## Download

``` groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "com.github.YarikSOffice:lingver:1.3.0"
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
