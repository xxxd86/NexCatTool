plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.nexify.layoutspeclibrary"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        //开启 Compose 支持
        viewBinding  = true
        dataBinding  = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.com.willowtreeapps.hyperion.hyperion.core3)
    releaseImplementation(libs.com.willowtreeapps.hyperion.hyperion.core.no.op)
    debugImplementation (libs.com.willowtreeapps.hyperion.hyperion.core3)
    debugImplementation (libs.com.willowtreeapps.hyperion.hyperion.attr)
    debugImplementation (libs.com.willowtreeapps.hyperion.hyperion.build.config9)
    debugImplementation  (libs.hyperion.crash)
    debugImplementation (libs.hyperion.disk)
    debugImplementation (libs.hyperion.geiger.counter)
    debugImplementation (libs.hyperion.measurement)
    debugImplementation  (libs.hyperion.phoenix)
    debugImplementation  (libs.hyperion.recorder)
    debugImplementation  (libs.hyperion.shared.preferences)
    debugImplementation  (libs.hyperion.timber)
    implementation(libs.androidx.lifecycle.extensions)
    implementation ("com.airbnb.android:lottie:6.6.0")
}