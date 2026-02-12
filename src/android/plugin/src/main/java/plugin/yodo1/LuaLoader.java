package plugin.yodo1;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.yodo1.mas.Yodo1MasSdkConfiguration;
import com.yodo1.mas.ad.Yodo1MasAdValue;
import com.yodo1.mas.appopenad.Yodo1MasAppOpenAd;
import com.yodo1.mas.appopenad.Yodo1MasAppOpenAdListener;
import com.yodo1.mas.appopenad.Yodo1MasAppOpenAdRevenueListener;
import com.yodo1.mas.banner.Yodo1MasBannerAdListener;
import com.yodo1.mas.banner.Yodo1MasBannerAdRevenueListener;
import com.yodo1.mas.banner.Yodo1MasBannerAdSize;
import com.yodo1.mas.banner.Yodo1MasBannerAdView;
import com.yodo1.mas.error.Yodo1MasError;
import com.yodo1.mas.helper.model.Yodo1MasAdBuildConfig;
import com.yodo1.mas.interstitial.Yodo1MasInterstitialAd;
import com.yodo1.mas.interstitial.Yodo1MasInterstitialAdListener;
import com.yodo1.mas.interstitial.Yodo1MasInterstitialAdRevenueListener;
import com.yodo1.mas.reward.Yodo1MasRewardAd;
import com.yodo1.mas.reward.Yodo1MasRewardAdListener;
import com.yodo1.mas.reward.Yodo1MasRewardAdRevenueListener;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class LuaLoader implements JavaFunction {
    private int fListener = CoronaLua.REFNIL;
    private static final String EVENT_NAME = "yodo1";
    private static int bannerAlign = 0;
    private boolean masInitCompleted = false;
    private Yodo1MasBannerAdView bannerAdView;

    @Override
    public int invoke(LuaState L) {
        NamedJavaFunction[] luaFunctions = new NamedJavaFunction[]{
                new Init(),
                new ShowBanner(),
                new HideBanner(),
                new SetBannerAlign(),
                new ShowInterstitial(),
                new ShowRewardedVideo(),
                new ShowAppOpen(),
                new IsInterstitialLoaded(),
                new IsRewardedVideoLoaded(),
                new IsAppOpenLoaded(),
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

    public void dispatchRevenueEvent(final String type, final double revenue, final String currency, final String revenuePrecision) {
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

                    L.pushString("revenue");
                    L.setField(-2, "phase");

                    L.pushNumber(revenue);
                    L.setField(-2, "revenue");

                    if (currency != null) {
                        L.pushString(currency);
                        L.setField(-2, "currency");
                    }

                    if (revenuePrecision != null) {
                        L.pushString(revenuePrecision);
                        L.setField(-2, "revenuePrecision");
                    }

                    CoronaLua.dispatchEvent(L, fListener, 0);
                } catch (Exception ignored) {
                }
            }
        });
    }

    private final Yodo1MasBannerAdListener bannerAdListener = new Yodo1MasBannerAdListener() {
        @Override
        public void onBannerAdLoaded(Yodo1MasBannerAdView bannerAdView) {
            dispatchEvent("banner", "loaded");
        }

        @Override
        public void onBannerAdFailedToLoad(Yodo1MasBannerAdView bannerAdView, @NonNull Yodo1MasError error) {
            dispatchEvent("banner", "failedToLoad", error.toString());
        }

        @Override
        public void onBannerAdOpened(Yodo1MasBannerAdView bannerAdView) {
            dispatchEvent("banner", "opened");
        }

        @Override
        public void onBannerAdFailedToOpen(Yodo1MasBannerAdView bannerAdView, @NonNull Yodo1MasError error) {
            dispatchEvent("banner", "failedToOpen", error.toString());
        }

        @Override
        public void onBannerAdClosed(Yodo1MasBannerAdView bannerAdView) {
            dispatchEvent("banner", "closed");
        }
    };

    private final Yodo1MasInterstitialAdListener interstitialAdListener = new Yodo1MasInterstitialAdListener() {
        @Override
        public void onInterstitialAdLoaded(Yodo1MasInterstitialAd ad) {
            dispatchEvent("interstitial", "loaded");
        }

        @Override
        public void onInterstitialAdFailedToLoad(Yodo1MasInterstitialAd ad, @NonNull Yodo1MasError error) {
            dispatchEvent("interstitial", "failedToLoad", error.toString());
            reloadInterstitial();
        }

        @Override
        public void onInterstitialAdOpened(Yodo1MasInterstitialAd ad) {
            dispatchEvent("interstitial", "opened");
        }

        @Override
        public void onInterstitialAdFailedToOpen(Yodo1MasInterstitialAd ad, @NonNull Yodo1MasError error) {
            dispatchEvent("interstitial", "failedToOpen", error.toString());
            reloadInterstitial();
        }

        @Override
        public void onInterstitialAdClosed(Yodo1MasInterstitialAd ad) {
            dispatchEvent("interstitial", "closed");
            reloadInterstitial();
        }
    };

    private final Yodo1MasInterstitialAdRevenueListener interstitialAdRevenueListener = new Yodo1MasInterstitialAdRevenueListener() {
        @Override
        public void onInterstitialAdPayRevenue(Yodo1MasInterstitialAd ad, Yodo1MasAdValue adValue) {
            dispatchRevenueEvent("interstitial", adValue.getRevenue(), adValue.getCurrency(), adValue.getRevenuePrecision());
        }
    };

    private final Yodo1MasRewardAdListener rewardAdListener = new Yodo1MasRewardAdListener() {
        @Override
        public void onRewardAdLoaded(Yodo1MasRewardAd ad) {
            dispatchEvent("reward", "loaded");
        }

        @Override
        public void onRewardAdFailedToLoad(Yodo1MasRewardAd ad, @NonNull Yodo1MasError error) {
            dispatchEvent("reward", "failedToLoad", error.toString());
            reloadReward();
        }

        @Override
        public void onRewardAdOpened(Yodo1MasRewardAd ad) {
            dispatchEvent("reward", "opened");
        }

        @Override
        public void onRewardAdFailedToOpen(Yodo1MasRewardAd ad, @NonNull Yodo1MasError error) {
            dispatchEvent("reward", "failedToOpen", error.toString());
            reloadReward();
        }

        @Override
        public void onRewardAdClosed(Yodo1MasRewardAd ad) {
            dispatchEvent("reward", "closed");
            reloadReward();
        }

        @Override
        public void onRewardAdEarned(Yodo1MasRewardAd ad) {
            dispatchEvent("reward", "earned");
        }
    };

    private final Yodo1MasRewardAdRevenueListener rewardAdRevenueListener = new Yodo1MasRewardAdRevenueListener() {
        @Override
        public void onRewardAdPayRevenue(Yodo1MasRewardAd ad, Yodo1MasAdValue adValue) {
            dispatchRevenueEvent("reward", adValue.getRevenue(), adValue.getCurrency(), adValue.getRevenuePrecision());
        }
    };

    private final Yodo1MasAppOpenAdListener appOpenAdListener = new Yodo1MasAppOpenAdListener() {
        @Override
        public void onAppOpenAdLoaded(Yodo1MasAppOpenAd ad) {
            dispatchEvent("appOpen", "loaded");
        }

        @Override
        public void onAppOpenAdFailedToLoad(Yodo1MasAppOpenAd ad, @NonNull Yodo1MasError error) {
            dispatchEvent("appOpen", "failedToLoad", error.toString());
            reloadAppOpen();
        }

        @Override
        public void onAppOpenAdOpened(Yodo1MasAppOpenAd ad) {
            dispatchEvent("appOpen", "opened");
        }

        @Override
        public void onAppOpenAdFailedToOpen(Yodo1MasAppOpenAd ad, @NonNull Yodo1MasError error) {
            dispatchEvent("appOpen", "failedToOpen", error.toString());
            reloadAppOpen();
        }

        @Override
        public void onAppOpenAdClosed(Yodo1MasAppOpenAd ad) {
            dispatchEvent("appOpen", "closed");
            reloadAppOpen();
        }
    };

    private final Yodo1MasAppOpenAdRevenueListener appOpenAdRevenueListener = new Yodo1MasAppOpenAdRevenueListener() {
        @Override
        public void onAppOpenAdPayRevenue(Yodo1MasAppOpenAd ad, Yodo1MasAdValue adValue) {
            dispatchRevenueEvent("appOpen", adValue.getRevenue(), adValue.getCurrency(), adValue.getRevenuePrecision());
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
                    Yodo1MasAppOpenAd.getInstance().autoDelayIfLoadFail = true;
                    Yodo1MasRewardAd.getInstance().autoDelayIfLoadFail = true;
                    Yodo1MasInterstitialAd.getInstance().autoDelayIfLoadFail = true;

                    Yodo1Mas.getInstance().initMas(activity, appKey, new Yodo1Mas.InitListener() {
                        @Override
                        public void onMasInitSuccessful() {
                            handleMasInitSuccess(activity);
                        }

                        @Override
                        public void onMasInitSuccessful(Yodo1MasSdkConfiguration yodo1MasSdkInitInfo) {
                            handleMasInitSuccess(activity);
                        }

                        @Override
                        public void onMasInitFailed(@NonNull Yodo1MasError error) {
                            Log.e("Corona", "yodo1 error: " + error.getMessage());
                            dispatchEvent("init", "error", error.toString());
                        }
                    });
                }
            });

            return 0;
        }
    }

    protected class SetBannerAlign implements NamedJavaFunction {
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
            if (bannerAdView != null) {
                applyBannerLayoutParams(bannerAdView, activity);
            }
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
                    ensureBannerAdView(activity);
                    attachBannerAdView(activity);
                    bannerAdView.loadAd();
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
            final String placementId = extractPlacementId(L);
            if (placementId == null) {
                Log.e("Corona", "yodo1.showInterstitial(placementId) : placementId is required");
                return 0;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Yodo1MasInterstitialAd.getInstance().showAd(activity, placementId);
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

            final String placementId = extractPlacementId(L);
            if (placementId == null) {
                Log.e("Corona", "yodo1.showRewardedVideo(placementId) : placementId is required");
                return 0;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Yodo1MasRewardAd.getInstance().showAd(activity, placementId);
                }
            });
            return 0;
        }
    }

    protected class ShowAppOpen implements NamedJavaFunction {
        @Override
        public String getName() {
            return "showAppOpen";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }

            final String placementId = extractPlacementId(L);
            if (placementId == null) {
                Log.e("Corona", "yodo1.showAppOpen(placementId) : placementId is required");
                return 0;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Yodo1MasAppOpenAd.getInstance().showAd(activity, placementId);
                }
            });

            return 0;
        }
    }

    protected class HideBanner implements NamedJavaFunction {
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
                    detachBannerAdView();
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
            L.pushBoolean(Yodo1MasRewardAd.getInstance().isLoaded());
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
            L.pushBoolean(Yodo1MasInterstitialAd.getInstance().isLoaded());
            return 1;
        }
    }

    protected static class IsAppOpenLoaded implements NamedJavaFunction {
        @Override
        public String getName() {
            return "isAppOpenLoaded";
        }

        @Override
        public int invoke(LuaState L) {
            final CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
            if (activity == null) {
                return 0;
            }
            L.pushBoolean(Yodo1MasAppOpenAd.getInstance().isLoaded());
            return 1;
        }
    }

    private void handleMasInitSuccess(CoronaActivity activity) {
        if (masInitCompleted) {
            return;
        }
        masInitCompleted = true;
        Log.i("Corona", "yodo1.init() success");
        dispatchEvent("init", "success");
        Yodo1MasAppOpenAd.getInstance().setAdListener(appOpenAdListener);
        Yodo1MasAppOpenAd.getInstance().setAdRevenueListener(appOpenAdRevenueListener);
        Yodo1MasRewardAd.getInstance().setAdListener(rewardAdListener);
        Yodo1MasRewardAd.getInstance().setAdRevenueListener(rewardAdRevenueListener);
        Yodo1MasInterstitialAd.getInstance().setAdListener(interstitialAdListener);
        Yodo1MasInterstitialAd.getInstance().setAdRevenueListener(interstitialAdRevenueListener);
        Yodo1MasAppOpenAd.getInstance().loadAd(activity);
        Yodo1MasRewardAd.getInstance().loadAd(activity);
        Yodo1MasInterstitialAd.getInstance().loadAd(activity);
    }

    private void reloadInterstitial() {
        CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
        if (activity == null) {
            return;
        }
        Yodo1MasInterstitialAd.getInstance().loadAd(activity);
    }

    private void reloadReward() {
        CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
        if (activity == null) {
            return;
        }
        Yodo1MasRewardAd.getInstance().loadAd(activity);
    }

    private void reloadAppOpen() {
        CoronaActivity activity = CoronaEnvironment.getCoronaActivity();
        if (activity == null) {
            return;
        }
        Yodo1MasAppOpenAd.getInstance().loadAd(activity);
    }

    private void ensureBannerAdView(CoronaActivity activity) {
        if (bannerAdView != null) {
            return;
        }
        bannerAdView = new Yodo1MasBannerAdView(activity);
        bannerAdView.setAdSize(Yodo1MasBannerAdSize.Banner);
        bannerAdView.setAdListener(bannerAdListener);
        bannerAdView.setAdRevenueListener(new Yodo1MasBannerAdRevenueListener() {
            @Override
            public void onBannerAdPayRevenue(Yodo1MasBannerAdView view, Yodo1MasAdValue adValue) {
                dispatchRevenueEvent("banner", adValue.getRevenue(), adValue.getCurrency(), adValue.getRevenuePrecision());
            }
        });
    }

    private void attachBannerAdView(CoronaActivity activity) {
        if (bannerAdView == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        if (parent == null) {
            return;
        }
        if (bannerAdView.getParent() != null) {
            applyBannerLayoutParams(bannerAdView, activity);
            return;
        }
        FrameLayout.LayoutParams params = buildBannerLayoutParams(activity);
        parent.addView(bannerAdView, params);
    }

    private void detachBannerAdView() {
        if (bannerAdView == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) bannerAdView.getParent();
        if (parent != null) {
            parent.removeView(bannerAdView);
        }
    }

    private void applyBannerLayoutParams(Yodo1MasBannerAdView bannerAdView, CoronaActivity activity) {
        ViewGroup.LayoutParams layoutParams = bannerAdView.getLayoutParams();
        if (!(layoutParams instanceof FrameLayout.LayoutParams)) {
            return;
        }
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layoutParams;
        params.width = dpToPx(activity, 320);
        params.height = dpToPx(activity, 50);
        params.gravity = resolveBannerGravity();
        bannerAdView.setLayoutParams(params);
    }

    private FrameLayout.LayoutParams buildBannerLayoutParams(CoronaActivity activity) {
        int width = dpToPx(activity, 320);
        int height = dpToPx(activity, 50);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = resolveBannerGravity();
        return params;
    }

    private int resolveBannerGravity() {
        int gravity = 0;
        if ((bannerAlign & Yodo1Mas.BannerLeft) != 0) {
            gravity |= Gravity.START;
        }
        if ((bannerAlign & Yodo1Mas.BannerRight) != 0) {
            gravity |= Gravity.END;
        }
        if ((bannerAlign & Yodo1Mas.BannerHorizontalCenter) != 0 || (gravity & (Gravity.START | Gravity.END)) == 0) {
            gravity |= Gravity.CENTER_HORIZONTAL;
        }
        if ((bannerAlign & Yodo1Mas.BannerTop) != 0) {
            gravity |= Gravity.TOP;
        }
        if ((bannerAlign & Yodo1Mas.BannerBottom) != 0) {
            gravity |= Gravity.BOTTOM;
        }
        if ((bannerAlign & (Yodo1Mas.BannerTop | Yodo1Mas.BannerBottom)) == 0 && (bannerAlign & Yodo1Mas.BannerVerticalCenter) != 0) {
            gravity |= Gravity.CENTER_VERTICAL;
        }
        if ((gravity & (Gravity.TOP | Gravity.BOTTOM | Gravity.CENTER_VERTICAL)) == 0) {
            gravity |= Gravity.BOTTOM;
        }
        return gravity;
    }

    private int dpToPx(CoronaActivity activity, int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, activity.getResources().getDisplayMetrics()));
    }

    private String extractPlacementId(LuaState L) {
        if (L.getTop() < 1 || !L.isString(1)) {
            return null;
        }
        String placementId = L.toString(1);
        if (placementId == null || placementId.trim().length() == 0) {
            return null;
        }
        return placementId;
    }

}
