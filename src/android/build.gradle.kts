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
        mavenCentral()

        maven( url = "https://artifact.bytedance.com/repository/pangle" )
        maven( url = "https://android-sdk.is.com" )
        maven( url = "https://sdk.tapjoy.com/" )

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
