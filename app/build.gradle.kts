plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ramphal.healthchecker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ramphal.healthchecker"
        minSdk = 23
        targetSdk = 34
        versionCode = 14
        versionName = "14.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dependencies {

        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)

        implementation(libs.play.services.ads)
        implementation(libs.core.splashscreen)
    }
}