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

local function addButtonLogReturnvalue(label, fnc)
	addButton(label, function()
		log( fnc() )
	end)
end

local function listener(event)
	log(tostring(event.type) .. ": " .. tostring(event.phase) .. ": " .. tostring(event.isError and event.errorType))
end

--[[
    Yodo1 MAS plugin 4.7.7

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

]]--
yodo1.init(listener, {
    appKey = "Ht0csvqMQnH",
    gdprConsent = false,
    ccpaConsent = false,
    coppaConsent = false,
    adaptiveBannerEnabled = true,
    privacyDialogEnabled = true
})


addButton("showBanner", yodo1.showBanner)
addButton("showInterstitial", yodo1.showInterstitial)
addButton("showRewardedVideo", yodo1.showRewardedVideo)
addButtonLogReturnvalue("hideBanner", yodo1.hideBanner)
addButtonLogReturnvalue("isRewardedVideoLoaded", yodo1.isRewardedVideoLoaded)
addButtonLogReturnvalue("isInterstitialLoaded", yodo1.isInterstitialLoaded)
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
