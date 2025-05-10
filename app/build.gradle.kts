plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)    
}
android {
    namespace = "com.example.cebolaolotofacilgenerator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cebolaolotofacilgenerator"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    packaging {
        // Check for version compatibility between Gradle, Android Plugin, and NDK.        

        jniLibs {
            keepDebugSymbols.add("libandroidx.graphics.path.so")
            keepDebugSymbols.add("libdatastore_shared_counter.so")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.activity.ktx)
    debugImplementation(libs.androidx.ui.tooling.preview)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    // Lifecycle + ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Gson
    implementation(libs.com.google.code.gson)
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
}
