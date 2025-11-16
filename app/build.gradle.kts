import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    id("com.google.gms.google-services")
}

// --- Load keystore values manually from secrets.properties (root folder) ---
val keystoreProps = Properties()
val secretsFile = rootProject.file("secrets.properties")
if (secretsFile.exists()) {
    keystoreProps.load(FileInputStream(secretsFile))
} else {
    println("WARNING: secrets.properties not found at project root")
}

android {
    namespace = "com.app.workahomie"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.workahomie"
        minSdk = 31
        targetSdk = 35
        versionCode = 2
        versionName = "1.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["auth0Domain"] = "@string/com_auth0_domain"
        manifestPlaceholders["auth0Scheme"] = "@string/com_auth0_scheme"

        buildConfigField("String", "MAPS_API_KEY", "\"${project.findProperty("MAPS_API_KEY")}\"")
        buildConfigField("String", "AUTH0_CLIENT_ID", "\"${project.findProperty("AUTH0_CLIENT_ID")}\"")
    }
    signingConfigs {
        create("release") {
            // Load values from keystoreProps
            val storePath = keystoreProps.getProperty("RELEASE_STORE_FILE")

            if (storePath != null) {
                storeFile = file(storePath)
            } else {
                println("❌ ERROR: RELEASE_STORE_FILE missing in secrets.properties")
            }

            storePassword = keystoreProps.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = keystoreProps.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = keystoreProps.getProperty("RELEASE_KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            // Optionally, use the same signing config for easier testing
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

secrets {
    propertiesFileName = "secrets.properties"

    defaultPropertiesFileName = "local.defaults.properties"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.auth0.android:auth0:3.7.0")

    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")

    // Retrofit
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // ViewModel
    val lifecycle_version = "2.9.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")

    // Foundation - Pager component
    implementation("androidx.compose.foundation:foundation:1.8.2")

    // Navigation
    val nav_version = "2.9.0"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("androidx.compose.ui:ui-text-google-fonts:1.8.1")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.maps.android:maps-compose:6.4.1")
    implementation("com.google.android.libraries.places:places:3.5.0")

    // Icons
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-messaging")
}