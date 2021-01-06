-- AdMob plugin

local Library = require "CoronaLibrary"

-- Create library
local lib = Library:new{ name="plugin.yodo1", publisherId="com.solar2d", version=1 }

-------------------------------------------------------------------------------
-- BEGIN
-------------------------------------------------------------------------------

-- This sample implements the following Lua:
-- 
--    local admob = require "plugin.admob"
--    admob.init()
--    

local function showWarning(functionName)
    print( functionName .. " WARNING: The AdMob plugin is only supported on Android & iOS devices. Please build for device")
end

function lib.init()
    showWarning("admob.init()")
end

function lib.showBanner()
    showWarning("admob.showBanner()")
end

function lib.showInterstitial()
    showWarning("admob.showInterstitial()")
end

function lib.showRewardedVideo()
    showWarning("admob.showRewardedVideo()")
end

function lib.hideBanner()
    showWarning("admob.hideBanner()")
end

function lib.isBannerLoaded()
    showWarning("admob.isBannerLoaded()")
end

function lib.isRewardedVideoLoaded()
    showWarning("admob.isRewardedVideoLoaded()")
end

function lib.isInterstitialLoaded()
    showWarning("admob.isInterstitialLoaded()")
end

function lib.setBannerAlign()
    showWarning("admob.setBannerAlign()")
end

-------------------------------------------------------------------------------
-- END
-------------------------------------------------------------------------------

-- Return an instance
return lib
