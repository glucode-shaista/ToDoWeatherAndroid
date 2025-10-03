//Hide API Key
import java.util.Properties
import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //id("com.google.devtools.ksp")
    alias(libs.plugins.ksp) //activates ksp
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}
//Hide API Key
val localProps = Properties().apply {
    val f = File(rootProject.rootDir, "local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

val weatherApiKey: String = localProps.getProperty("WEATHER_API_KEY")
    ?: System.getenv("WEATHER_API_KEY")
    ?: ""

if (weatherApiKey.isEmpty()) {
    logger.warn("WEATHER_API_KEY not set. Build will succeed, but API calls may fail.")
}

android {
    namespace = "com.example.todoapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.todoapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Makes BuildConfig.WEATHER_API_KEY available in code - Hide API Key
        buildConfigField("String", "WEATHER_API_KEY", "\"$weatherApiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.media3.common.ktx)
    
    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.test.ext.junit.ktx)
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Lifecycle ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room + KSP
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Location
    implementation(libs.play.services.location)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Compose Material (provides pullRefresh APIs
    implementation(libs.compose.material)


    //Navigation
    implementation(libs.androidx.navigation.compose)

    implementation(libs.gson)

    implementation(libs.coil.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}