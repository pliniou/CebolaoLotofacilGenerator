plugins {
    alias(libs.plugins.android.application) apply false
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
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    testImplementation("androidx.test.ext:junit")

    androidTestImplementation("androidx.test.ext:junit")
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.coroutines.core)    
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.ui:ui-graphics:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
    
    implementation(libs.androidx.activity.ktx)

    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    ksp(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.ui.tooling.preview)    
}
