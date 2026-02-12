buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.7.20"))
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("com.google.code.gson:gson:2.10.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()

        maven { url = uri("https://artifact.bytedance.com/repository/pangle") }
        maven { url = uri("https://android-sdk.is.com") }
        maven { url = uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea") }
        maven { url = uri("https://artifactory.bidmachine.io/bidmachine") }
        maven { url = uri("https://ysonetwork.s3.eu-west-3.amazonaws.com/sdk/android") }
        maven { url = uri("https://repo.pubmatic.com/artifactory/public-repos") }
        maven { url = uri("https://cboost.jfrog.io/artifactory/chartboost-ads/") }

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
