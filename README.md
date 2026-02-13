# YODO1 MAS plugin

Yodo1 support site: https://developers.yodo1.com/docs/sdk/guides/android/integration

Create Yodo1 MAS Account: https://mas.yodo1.com/register


## Setup `build.settings`


```lua
settings =
{
    android =
    {
        minSdkVersion = "24",
        applicationChildElements =
        {
            [[
                <meta-data android:name="com.google.android.gms.ads.APPLICATION_ID"
                    android:value="[YOUR_ADMOB_APP_ID]"/>  -- replace with your app id. See: https://goo.gl/fQ2neu
            ]],
        },
    },
    plugins = 
    {
        ["plugin.yodo1"] = { publisherId = "com.solar2d" },
    }, 
}
```

## Docs

```
    Yodo1 MAS plugin 4.17.1
    Android requirements: minSdkVersion 24, targetSdkVersion 34, compileSdkVersion 34

    appKey = "your_appKey" (required)
        Must match the appKey for this app in your Yodo1 account

    ccpaConsent = false
        If the user chooses to opt in to targeted advertising
        See https://developers.yodo1.com/knowledge-base/faq/

    gdprConsent = false
        If the user chooses to opt in to targeted advertising and tracking
        See https://developers.yodo1.com/knowledge-base/faq/

    coppaConsent = false
        Only set true if this app is targeted ONLY to children under 13
        See https://developers.yodo1.com/knowledge-base/coppa-compliance/

    adaptiveBannerEnabled = true
        Enable adaptive banner ads. May not be available on all platforms
        or devices. If enabled ensure adaptive ads are enabled for this appKey.
        See https://developers.yodo1.com/knowledge-base/ad-units/#Banner

    privacyDialogEnabled = true
        Enables the Yodo1 privacy dialog box, asking the user for their age.

    userAgreementUrl = ""
        If you enable the Yodo1 privacy dialog, you can supply your own user agreement URL

    privacyPolicyUrl = ""
        If you enable the Yodo1 privacy dialog, you can supply your own privacy policy URL
```

## Sample app code

```lua
local yodo1 = require 'plugin.yodo1'

local function Yodo1Listener(event) -- event.name = "yodo1"
    print("Yodo Event", event.type, event.phase, event.isError and event.errorType)
end

yodo1.init(Yodo1Listener, {
    appKey = "yourappkey",
    gdprConsent = true,
    ccpaConsent = false,
    coppaConsent = false,
    adaptiveBannerEnabled = true,
    privacyDialogEnabled = false
})

yodo1.showBanner()
yodo1.hideBanner()
-- this method accepts any number of arguments. Leave only those which make sense
yodo1.setBannerAlign("left", "horizontalCenter", "right", "top", "verticalCenter", "bottom")

yodo1.showInterstitial("YourPlacementId")
yodo1.showRewardedVideo("YourPlacementId")
yodo1.showAppOpen("YourPlacementId")

yodo1.isRewardedVideoLoaded() -- returns boolean
yodo1.isInterstitialLoaded() -- returns boolean
yodo1.isAppOpenLoaded() -- returns boolean

```

### Event `yodo1`

| Function                | Type              | Phases                               |
| ----------------------- | ----------------- | -------------------------------------|
| `init()`                | `init`            | `success`,`error`                    |
| `showBanner()`          | `banner`          | `loaded`, `failedToLoad`, `opened`, `failedToOpen`, `closed` |
| `showInterstitial()`    | `interstitial`    | `loaded`, `failedToLoad`, `opened`, `failedToOpen`, `closed` |
| `showRewardedVideo()`   | `reward`          | `loaded`, `failedToLoad`, `opened`, `failedToOpen`, `closed`, `earned` |
| `showAppOpen()`         | `appOpen`         | `loaded`, `failedToLoad`, `opened`, `failedToOpen`, `closed` |
| `*`                     | `banner`, `interstitial`, `reward`, `appOpen` | `revenue` |

Events with phase "failedToLoad" or "failedToOpen" have `isError` set to `true` and `errorType` to the error string.
`showInterstitial()`, `showRewardedVideo()`, and `showAppOpen()` require a placement ID string.
Revenue events use `phase = "revenue"` and include `revenue` (number), `currency` (string), and `revenuePrecision` (string).


## Updating this plugin

To future maintainers of this plugin:

* update the version of Yodo1 MAS API in these files:
  * `README.md`
  * `plugins/2020.3607/android/corona.gradle`
  * `src/android/plugin/build.gradle`
* make code changes in `src/android/plugin/src/main/java/plugin/yodo1/LuaLoader.java` to support the updated API
* build the AAR:
  ```bash
  cd src/android
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
  ./gradlew :plugin:assembleRelease
  ```
* copy the compiled AAR into the plugin directory:
  ```bash
  cp src/android/plugin/build/outputs/aar/plugin-release.aar plugins/2020.3607/android/yodo1.aar
  ```

## Publishing to Solar2D Plugin Directory

1. Commit and push all changes to GitHub
2. Submit plugin to [Solar2D Free Plugin Directory](https://plugins.solar2d.com/) or [Solar2D Plugins Marketplace](https://www.solar2dplugins.com/)
3. For the free directory, submit a PR to the [Solar2D plugins repository](https://github.com/solar2d/com.solar2d-plugins) with the updated `plugins/2020.3607/android/` folder contents (`corona.gradle`, `metadata.lua`, `yodo1.aar`)
4. For self-hosting, package the android folder as `android.tgz` and host it on your server (see [Solar2D Native docs](https://docs.coronalabs.com/native/android/index.html#building-for-self-hosted-plugins))
