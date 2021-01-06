# YODO1 plugin

Yodo MAS plugin: https://support.yodo1.com/hc/en-us

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
                <meta-data
                    android:name="Yodo1ChannelCode"
                    android:value="GooglePlay"
                    tools:replace="android:value" />
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

```lua
local yodo1 = require 'plugin.yodo1'

local function Yodo1Listener(event) { -- event.name = "yodo1"
    print("Yodo Event", event.type, event.phase, event.isError and event.errorType)
}

yodo1.init(Yodo1Listener, {
    appKey = 'aaabbbccc',
    debug = false,
    userConsent = true, -- GDPR consent https://support.yodo1.com/hc/en-us/articles/360051531234
    doNotSell = false, -- CCPA consent https://support.yodo1.com/hc/en-us/articles/360052314493
    tagForUnderAgeOfConsent = false, -- COPPA consent https://support.yodo1.com/hc/en-us/articles/360051535114
})
yodo1.showBanner()
yodo1.hideBanner()
yodo1.setBannerAlign( "left", "horizontalCenter", "right", "top", "verticalCenter", "bottom" )  -- this method accepts any number of arguments. Leave only those which make sense
yodo1.showInterstitial()
yodo1.showRewardedVideo()

yodo1.isBannerLoaded() -- returns boolean 
yodo1.isRewardedVideoLoaded() -- returns boolean
yodo1.isInterstitialLoaded() -- returns boolean

```

### Event `yodo1`

| Function                | Type                | Phases                                                           |
| ----------------------- | ------------------- | ---------------------------------------------------------------- |
| `init()`                | `"init"`            | `"init"`                                                         |     
| `showBanner()`          | `"banner"`          | `"closed"`, `"displayed"`, `""failed"`, `"clicked"`              |
| `showInterstitial()`    | `"interstitial"`    | `"closed"`, `"displayed"`, `""failed"`, `"clicked"`              |
| `showRewardedVideo()`   | `"rewardedVideo"`   | `"closed"`, `"displayed"`, `""failed"`, `"clicked"`, `"reward"`  |

Events with phase `"failed"` have `isError` set to `true` and `errorType` to the error string.
