plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.skycastproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.skycastproject"
        minSdk = 24
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

    compileOptions {
        // Enforces Java 17 compilation properties for Gradle 9 runtime compliance
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core AndroidX and Lifecycle Extensions
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose Framework Integration
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    // Material Design Extended Asset Packs
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Jetpack WorkManager Background Engine
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // Google Play Services Location Engine (Formatted Accessor)
    implementation(libs.google.play.services.location)
    implementation(libs.okhttp.core)

    // Jetpack Glance Widget Layout Framework
    implementation(libs.androidx.glance.widget)

    // Room Persistent Database Local Engine
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Handles suspend function calls cleanly inside DAOs
    ksp(libs.androidx.room.compiler)

    // Retrofit Type-Safe REST Engine & Native Kotlinx Parsing
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.serialization)
    implementation(libs.kotlinx.serialization.json)

    // Dagger Hilt Dependency Injection Infrastructure
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work) // Link framework for WorkManager background injections
    ksp(libs.androidx.hilt.compiler)

    // Local JVM Unit Analysis Tests
    testImplementation(libs.junit)
    implementation(libs.androidx.hilt.navigation.compose)

    // Instrumented Android Architecture Integration Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Local Debug/Layout Component Preview Utilities
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}