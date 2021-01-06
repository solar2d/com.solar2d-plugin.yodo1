buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.3.70"))
        classpath("com.android.tools.build:gradle:3.5.4")
        classpath("com.beust:klaxon:5.0.1")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven( url = "https://dl.bintray.com/yodo1/android-sdk")
        maven( url = "https://jitpack.io")
        maven( url = "https://applovin.bintray.com/Android-Adapter-SDKs")
        maven( url = "https://dl.bintray.com/ironsource-mobile/android-sdk")
//        maven( url = "https://chartboostmobile.bintray.com/Chartboost")
        maven( url = "http://dl.appnext.com/")
        maven( url = "https://fyber.bintray.com/marketplace")
        maven( url = "https://dl.bintray.com/mintegral-official/mintegral_ad_sdk_android_for_oversea")
        val nativeDir = if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.getenv("CORONA_ROOT")
        } else {
            "${System.getenv("HOME")}/Library/Application Support/Corona/Native/"
        }
        flatDir {
            dirs("$nativeDir/Corona/android/lib/gradle", "$nativeDir/Corona/android/lib/Corona/libs")
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
