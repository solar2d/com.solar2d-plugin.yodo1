package plugin.yodo1;

import android.util.Log;

import com.ansca.corona.CoronaActivity;
import com.ansca.corona.CoronaEnvironment;
import com.ansca.corona.CoronaLua;
import com.ansca.corona.CoronaLuaEvent;
import com.ansca.corona.CoronaRuntime;
import com.ansca.corona.CoronaRuntimeTask;
import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaType;
import com.naef.jnlua.NamedJavaFunction;
import com.yodo1.advert.banner.Yodo1BannerAlign;
import com.yodo1.advert.callback.BannerCallback;
import com.yodo1.advert.callback.InterstitialCallback;
import com.yodo1.advert.callback.VideoCallback;
import com.yodo1.advert.entity.AdErrorCode;
import com.yodo1.advert.open.Yodo1Advert;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class LuaLoader implements JavaFunction {
    private int fListener = CoronaLua.REFNIL;
    private static final String EVENT_NAME = "yodo1";

    @Override
    public int invoke(LuaState L) {
        NamedJavaFunction[] luaFunctions = new NamedJavaFunction[]{
                new Init(),
                new ShowBanner(),
                new HideBanner(),
                new SetBannerAlign(),
                new ShowInterstitial(),
                new ShowRewardedVideo(),
                new IsBannerLoaded(),
                new IsInterstitialLoaded(),
                new IsRewardedVideoLoaded(),
        };
        String libName = L.toString(1);
        L.register(libName, luaFunctions);
        return 1;
    }

    public void dispatchEvent(final String type, final String phase) {
        dispatchEvent(type, phase, null);
    }

    public void dispatchEvent(final String type, final String phase, final String error) {
        CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
        if (activity == null) return;
        activity.getRuntimeTaskDispatcher().send(new CoronaRuntimeTask() {
            @Override
            public void executeUsing(CoronaRuntime runtime) {
                try {
                    LuaState L = runtime.getLuaState();
                    if (L == null) return;

                    CoronaLua.newEvent(L, EVENT_NAME);

                    L.pushString(type);
                    L.setField(-2, "type");

                    L.pushString(phase);
                    L.setField(-2, "phase");

                    if (error != null) {
                        L.pushBoolean(true);
                        L.setField(-2, CoronaLuaEvent.ISERROR_KEY);

                        L.pushString(error);
                        L.setField(-2, CoronaLuaEvent.ERRORTYPE_KEY);
                    }

                    CoronaLua.dispatchEvent(L, fListener, 0);
                } catch (Exception ignored) {
                }
            }
        });
    }

    protected class Init implements NamedJavaFunction {
        @Override
        public String getName() {
            return "init";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }

            if (!L.isTable(2)) {
                Log.e("Corona", "yodo1.init(parameters), parameters is missing");
                return 0;
            }

            if (CoronaLua.isListener(L, 1, EVENT_NAME)) {
                fListener = CoronaLua.newRef(L, 1);
            }

            final String sdkCode;
            L.getField(2, "appKey");
            if (L.isString(-1)) {
                sdkCode = L.toString(-1);
            } else {
                sdkCode = null;
            }
            L.pop(1);
            if (sdkCode == null) {
                Log.e("Corona", "yodo1.init(listener, key), key is not a string");
                return 0;
            }

            L.getField(2, "userConsent");
            if (L.type(-1) == LuaType.BOOLEAN) {
                Yodo1Advert.setUserConsent(L.toBoolean(-1));
            }
            L.pop(1);

            L.getField(2, "doNotSell");
            if (L.type(-1) == LuaType.BOOLEAN) {
                Yodo1Advert.setDoNotSell(L.toBoolean(-1));
            }
            L.pop(1);

            L.getField(2, "tagForUnderAgeOfConsent");
            if (L.type(-1) == LuaType.BOOLEAN) {
                Yodo1Advert.setTagForUnderAgeOfConsent(L.toBoolean(-1));
            }
            L.pop(1);

            L.getField(2, "debug");
            if (L.toBoolean(-1)) {
                Yodo1Advert.setOnLog(true);
            }
            L.pop(1);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Yodo1Advert.initSDK(activity, sdkCode);
                    dispatchEvent("init", "init");
                }
            });

            return 0;
        }
    }

    protected static class SetBannerAlign implements NamedJavaFunction {
        @Override
        public String getName() {
            return "setBannerAlign";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }

            int nArgs = L.getTop();
            int align = 0;
            for (int i = 0; i <= nArgs; i++) {
                if (L.isString(i)) {
                    switch (L.toString(i)) {
                        case "left":
                            align |= Yodo1BannerAlign.BannerAlignLeft;
                            break;
                        case "horizontalCenter":
                            align |= Yodo1BannerAlign.BannerAlignHorizontalCenter;
                            break;
                        case "right":
                            align |= Yodo1BannerAlign.BannerAlignRight;
                            break;
                        case "top":
                            align |= Yodo1BannerAlign.BannerAlignTop;
                            break;
                        case "verticalCenter":
                            align |= Yodo1BannerAlign.BannerAlignVerticalCenter;
                            break;
                        case "bottom":
                            align |= Yodo1BannerAlign.BannerAlignBottom;
                            break;
                    }
                }
            }
            final int finalAlign = align;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Yodo1Advert.setBannerAlign(activity, finalAlign);
                }
            });
            return 0;
        }
    }

    protected class ShowBanner implements NamedJavaFunction {
        @Override
        public String getName() {
            return "showBanner";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Yodo1Advert.showBanner(activity, new BannerCallback() {
                        @Override
                        public void onBannerClosed() {
                            dispatchEvent("banner", "closed");
                        }

                        @Override
                        public void onBannerShow() {
                            dispatchEvent("banner", "displayed");
                        }

                        @Override
                        public void onBannerShowFailed(AdErrorCode errorCode) {
                            dispatchEvent("banner", "failed", errorCode.name());
                        }

                        @Override
                        public void onBannerClicked() {
                            dispatchEvent("banner", "clicked");
                        }
                    });
                }
            });
            return 0;
        }
    }

    protected class ShowInterstitial implements NamedJavaFunction {
        @Override
        public String getName() {
            return "showInterstitial";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Yodo1Advert.showInterstitial(activity, new InterstitialCallback() {
                        @Override
                        public void onInterstitialClosed() {
                            dispatchEvent("interstitial", "closed");
                        }

                        @Override
                        public void onInterstitialShowSucceeded() {
                            dispatchEvent("interstitial", "displayed");
                        }

                        @Override
                        public void onInterstitialShowFailed(AdErrorCode adErrorCode) {
                            dispatchEvent("interstitial", "failed", adErrorCode.name());
                        }

                        @Override
                        public void onInterstitialClicked() {
                            dispatchEvent("interstitial", "clicked");
                        }
                    });
                }
            });

            return 0;
        }
    }

    protected class ShowRewardedVideo implements NamedJavaFunction {
        @Override
        public String getName() {
            return "showRewardedVideo";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Yodo1Advert.showVideo(activity, new VideoCallback() {
                        @Override
                        public void onVideoClosed(boolean isFinished) {
                            dispatchEvent("rewardedVideo", "closed");
                            if (isFinished) {
                                dispatchEvent("rewardedVideo", "reward");
                            }
                        }

                        @Override
                        public void onVideoShow() {
                            dispatchEvent("rewardedVideo", "displayed");
                        }

                        @Override
                        public void onVideoShowFailed(AdErrorCode errorCode) {
                            dispatchEvent("rewardedVideo", "failed", errorCode.name());
                        }

                        @Override
                        public void onVideoClicked() {
                            dispatchEvent("rewardedVideo", "clicked");
                        }
                    });
                }
            });
            return 0;
        }
    }

    protected static class HideBanner implements NamedJavaFunction {
        @Override
        public String getName() {
            return "hideBanner";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Yodo1Advert.hideBanner(activity);
                }
            });
            return 0;
        }
    }

    protected static class IsBannerLoaded implements NamedJavaFunction {
        @Override
        public String getName() {
            return "isBannerLoaded";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }
            L.pushBoolean(Yodo1Advert.bannerIsReady(activity));
            return 1;
        }
    }

    protected static class IsRewardedVideoLoaded implements NamedJavaFunction {
        @Override
        public String getName() {
            return "isRewardedVideoLoaded";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }
            L.pushBoolean(Yodo1Advert.videoIsReady(activity));
            return 1;
        }
    }

    protected static class IsInterstitialLoaded implements NamedJavaFunction {
        @Override
        public String getName() {
            return "isInterstitialLoaded";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }
            L.pushBoolean(Yodo1Advert.interstitialIsReady(activity));
            return 1;
        }
    }

}
