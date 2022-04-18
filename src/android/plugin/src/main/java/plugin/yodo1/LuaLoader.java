package plugin.yodo1;

import android.util.Log;
import androidx.annotation.NonNull;

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

import com.yodo1.mas.Yodo1Mas;
import com.yodo1.mas.error.Yodo1MasError;
import com.yodo1.mas.event.Yodo1MasAdEvent;
import com.yodo1.mas.helper.Yodo1MasHelper;
import com.yodo1.mas.helper.model.Yodo1MasAdBuildConfig;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class LuaLoader implements JavaFunction {
    private int fListener = CoronaLua.REFNIL;
    private static final String EVENT_NAME = "yodo1";
    private static int bannerAlign = 0;

    @Override
    public int invoke(LuaState L) {
        NamedJavaFunction[] luaFunctions = new NamedJavaFunction[]{
                new Init(),
                new ShowBanner(),
                new HideBanner(),
                new SetBannerAlign(),
                new ShowInterstitial(),
                new ShowRewardedVideo(),
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

    private final Yodo1Mas.BannerListener bannerListener = new Yodo1Mas.BannerListener() {
        @Override
        public void onAdOpened(@NonNull Yodo1MasAdEvent event) {
            dispatchEvent("banner", "opened");
        }
        @Override
        public void onAdClosed(@NonNull Yodo1MasAdEvent event) {
            dispatchEvent("banner", "closed");
        }
        @Override
        public void onAdError(@NonNull Yodo1MasAdEvent event, @NonNull Yodo1MasError error) {
            dispatchEvent("banner", "error", error.toString());
        }
    };

    private final Yodo1Mas.RewardListener rewardListener = new Yodo1Mas.RewardListener() {
        @Override
        public void onAdOpened(@NonNull Yodo1MasAdEvent event) {
            dispatchEvent("reward", "opened");
        }
        @Override
        public void onAdvertRewardEarned(@NonNull Yodo1MasAdEvent event) {
            dispatchEvent("reward", "earned");
        }
        @Override
        public void onAdError(@NonNull Yodo1MasAdEvent event, @NonNull Yodo1MasError error) {
            dispatchEvent("reward", "error", error.toString());
        }
        @Override
        public void onAdClosed(@NonNull Yodo1MasAdEvent event) {
            dispatchEvent("reward", "closed");
        }
    };

    private final Yodo1Mas.InterstitialListener interstitialListener = new Yodo1Mas.InterstitialListener() {
        @Override
        public void onAdOpened(@NonNull Yodo1MasAdEvent event) {
            dispatchEvent("interstitial", "opened");
        }
        @Override
        public void onAdClosed(@NonNull Yodo1MasAdEvent event) {
            dispatchEvent("interstitial", "closed");
        }
        @Override
        public void onAdError(@NonNull Yodo1MasAdEvent event, @NonNull Yodo1MasError error) {
            dispatchEvent("interstitial", "error", error.toString());
        }
    };

    protected class Init implements NamedJavaFunction {
        @Override
        public String getName() {
            return "init";
        }

        @Override
        public int invoke(LuaState L) {
            boolean gdprConsent = false;
            boolean ccpaConsent = false;
            boolean coppaConsent = false;
            boolean adaptiveBannerEnabled = false;
            boolean privacyDialogEnabled = true;
            String userAgreementUrl = null;
            String privacyPolicyUrl = null;
            final String appKey;

            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }

            if (!L.isTable(2)) {
                Log.e("Corona", "yodo1.init(listener, {parameters}) : parameters table is missing");
                return 0;
            }

            if (CoronaLua.isListener(L, 1, EVENT_NAME)) {
                fListener = CoronaLua.newRef(L, 1);
            }

            L.getField(2, "appKey");
            if (L.isString(-1)) {
                appKey = L.toString(-1);
            } else {
                appKey = null;
            }
            L.pop(1);
            if (appKey == null) {
                Log.e("Corona", "yodo1.init() : appKey parameter is not a string");
                return 0;
            }

            L.getField(2, "gdprConsent");
            if (L.type(-1) == LuaType.BOOLEAN) {
                gdprConsent = L.toBoolean(-1);
            }
            L.pop(1);

            L.getField(2, "ccpaConsent");
            if (L.type(-1) == LuaType.BOOLEAN) {
                ccpaConsent = L.toBoolean(-1);
            }
            L.pop(1);

            L.getField(2, "coppaConsent");
            if (L.type(-1) == LuaType.BOOLEAN) {
                coppaConsent = L.toBoolean(-1);
            }
            L.pop(1);

            L.getField(2, "adaptiveBannerEnabled");
            if (L.type(-1) == LuaType.BOOLEAN) {
                adaptiveBannerEnabled = L.toBoolean(-1);
            }
            L.pop(1);

            L.getField(2, "privacyDialogEnabled");
            if (L.type(-1) == LuaType.BOOLEAN) {
                privacyDialogEnabled = L.toBoolean(-1);
            }
            L.pop(1);

            L.getField(2, "userAgreementUrl");
            if (L.isString(-1)) {
                userAgreementUrl = L.toString(-1);
            }
            L.pop(1);

            L.getField(2, "privacyPolicyUrl");
            if (L.isString(-1)) {
                privacyPolicyUrl = L.toString(-1);
            }
            L.pop(1);

            Yodo1MasAdBuildConfig.Builder builder = new Yodo1MasAdBuildConfig.Builder()
                    .enableAdaptiveBanner(adaptiveBannerEnabled)
                    .enableUserPrivacyDialog(privacyDialogEnabled);
            if (userAgreementUrl != null) {
                builder.userAgreementUrl(userAgreementUrl);
            }
            if (privacyPolicyUrl != null) {
                builder.privacyPolicyUrl(privacyPolicyUrl);
            }
            Yodo1MasAdBuildConfig config = builder.build();
            Yodo1Mas.getInstance().setAdBuildConfig(config);
            Yodo1Mas.getInstance().setCOPPA(coppaConsent);
            Yodo1Mas.getInstance().setGDPR(gdprConsent);
            Yodo1Mas.getInstance().setCCPA(ccpaConsent);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Yodo1Mas.getInstance().init(activity, appKey, new Yodo1Mas.InitListener() {
                        @Override
                        public void onMasInitSuccessful() {
                            Log.i("Corona", "yodo1.init() success");
                            dispatchEvent("init", "success");
                        }

                        @Override
                        public void onMasInitFailed(@NonNull Yodo1MasError error) {
                            Log.e("Corona", "yoda1 error: " + error.getMessage());
                            dispatchEvent("init", "error");
                        }
                    });

                    Yodo1Mas.getInstance().setRewardListener(rewardListener);
                    Yodo1Mas.getInstance().setInterstitialListener(interstitialListener);
                    Yodo1Mas.getInstance().setBannerListener(bannerListener);
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
                            align |= Yodo1Mas.BannerLeft;
                            break;
                        case "horizontalCenter":
                            align |= Yodo1Mas.BannerHorizontalCenter;
                            break;
                        case "right":
                            align |= Yodo1Mas.BannerRight;
                            break;
                        case "top":
                            align |= Yodo1Mas.BannerTop;
                            break;
                        case "verticalCenter":
                            align |= Yodo1Mas.BannerVerticalCenter;
                            break;
                        case "bottom":
                            align |= Yodo1Mas.BannerBottom;
                            break;
                    }
                }
            }
            bannerAlign = align;
            return 0;
        }
    }

    protected static class ShowBanner implements NamedJavaFunction {
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
                    Yodo1Mas.getInstance().showBannerAd(activity);
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
                    Yodo1Mas.getInstance().showInterstitialAd(activity);
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
                    Yodo1Mas.getInstance().showRewardedAd(activity);
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
                    Yodo1Mas.getInstance().dismissBannerAd();
                }
            });
            return 0;
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
            L.pushBoolean(Yodo1Mas.getInstance().isRewardedAdLoaded());
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
            L.pushBoolean(Yodo1Mas.getInstance().isInterstitialAdLoaded());
            return 1;
        }
    }

}
