# YODO1 MAS plugin

Version 4 of the Yodo1 API is not backwards compatible with version 3. If you used the old version of this plugin please check all of your methods and events with those listed below.

Yodo1 support site: https://support.yodo1.com

## Setup `build.settings`


```lua
settings =
{
    android =
    {
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
    Yodo1 MAS plugin 4.4.5

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

```lua
local yodo1 = require 'plugin.yodo1'

local function Yodo1Listener(event) { -- event.name = "yodo1"
    print("Yodo Event", event.type, event.phase, event.isError and event.errorType)
}

yodo1.init(Yodo1Listener, {
    appKey = "YourYodo1AppKey",
    gdprConsent = false,
    ccpaConsent = false,
    coppaConsent = false,
    adaptiveBannerEnabled = true,
    privacyDialogEnabled = true
})

yodo1.showBanner()
yodo1.hideBanner()
-- this method accepts any number of arguments. Leave only those which make sense
yodo1.setBannerAlign("left", "horizontalCenter", "right", "top", "verticalCenter", "bottom")

yodo1.showInterstitial()
yodo1.showRewardedVideo()

yodo1.isRewardedVideoLoaded() -- returns boolean
yodo1.isInterstitialLoaded() -- returns boolean

```

### Event `yodo1`

| Function                | Type                | Phases                                  |
| ----------------------- | ------------------- | --------------------------------------- |
| `init()`                | `"init"`            | `"success"`,`"error"`                   |
| `showBanner()`          | `"banner"`          | `"opened"`, `"closed"`, `"error"`       |
| `showInterstitial()`    | `"interstitial"`    | `"opened"`, `"closed"`, `"error"`       |
| `showRewardedVideo()`   | `"reward"`          | `"opened"`, `"earned"`, `"error"`       |

Events with phase `"error"` have `isError` set to `true` and `errorType` to the error string.
