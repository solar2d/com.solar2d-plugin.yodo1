local widget = require "widget"
local json = require "json"
local logText
local cnt = 1


local yodo1 = require "plugin.yodo1"

local curY = 30
local function addButton(label, action)
	local button = widget.newButton {
		label = label,
		onRelease  = action,
		emboss = false,
		shape = "roundedRect",
		width = 200,
		height = 20,
		cornerRadius = 2,
		fillColor = { default={1,0,0,1}, over={1,0.1,0.7,0.4} },
		strokeColor = { default={1,0.4,0,1}, over={0.8,0.8,1,1} },
		strokeWidth = 2,
	}
	button.x = display.contentCenterX
	button.y = curY
	curY = curY + button.height+10
end

local function log(t)
	print(t)
	logText.text = tostring(cnt) .. ": " .. tostring(t) .. "\n" .. logText.text
	cnt = cnt + 1
end

local function addButtonLR(label, fnc)
	addButton(label, function()
		log( fnc() )
	end)
end

local function listener(event)
	log(tostring(event.type) .. ": " .. tostring(event.phase) .. ": " .. tostring(event.isError and event.errorType))
end

yodo1.init(listener, {
    appKey = "MVWzsjaJDO9",
    debug = true,
    userConsent = true, -- GDPR consent https://support.yodo1.com/hc/en-us/articles/360051531234
    doNotSell = false, -- CCPA consent https://support.yodo1.com/hc/en-us/articles/360052314493
    tagForUnderAgeOfConsent = false, -- COPPA consent https://support.yodo1.com/hc/en-us/articles/360051535114
})



addButton("showBanner", yodo1.showBanner)
addButton("showInterstitial", yodo1.showInterstitial)
addButton("showRewardedVideo", yodo1.showRewardedVideo)
addButtonLR("hideBanner", yodo1.hideBanner)
addButtonLR("isBannerLoaded", yodo1.isBannerLoaded)
addButtonLR("isRewardedVideoLoaded", yodo1.isRewardedVideoLoaded)
addButtonLR("isInterstitialLoaded", yodo1.isInterstitialLoaded)
addButton("banner align: left top", function()
	yodo1.setBannerAlign( "left", "top")
end)
addButton("banner align: center", function()
	yodo1.setBannerAlign( "horizontalCenter", "verticalCenter")
end)

local h = display.contentHeight - curY
logText = native.newTextBox( display.contentCenterX, curY + h/2, 250, h )
logText.isEditable = false
logText.isFontSizeScaled = true
logText.size = 12
