-- AdMob plugin

local Library = require "CoronaLibrary"

-- Create library
local lib = Library:new{ name="plugin.yodo1", publisherId="com.solar2d", version=2 }

-------------------------------------------------------------------------------
-- BEGIN
-------------------------------------------------------------------------------

local function showWarning(functionName)
    print( functionName .. " WARNING: The Yodo1 plugin is only supported on Android & iOS devices. Please build for device")
end

function lib.init()
    showWarning("yodo1.init()")
end

function lib.showBanner()
    showWarning("yodo1.showBanner()")
end

function lib.showInterstitial()
    showWarning("yodo1.showInterstitial()")
end

function lib.showRewardedVideo()
    showWarning("yodo1.showRewardedVideo()")
end

function lib.hideBanner()
    showWarning("yodo1.hideBanner()")
end

function lib.isRewardedVideoLoaded()
    showWarning("yodo1.isRewardedVideoLoaded()")
end

function lib.isInterstitialLoaded()
    showWarning("yodo1.isInterstitialLoaded()")
end

function lib.setBannerAlign()
    showWarning("yodo1.setBannerAlign()")
end

-------------------------------------------------------------------------------
-- END
-------------------------------------------------------------------------------

-- Return an instance
return lib
