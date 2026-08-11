plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id(
        "com.google.android.libraries.mapsplatform.secrets-gradle-plugin"
    )
}

android {
    namespace = "com.homemadefood.app"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.homemadefood.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_11

        targetCompatibility =
            JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(
        platform(libs.androidx.compose.bom)
    )
    implementation(
        "com.google.maps.android:maps-compose:6.12.0"
    )


    implementation(
        libs.androidx.activity.compose
    )
    implementation(
        "com.google.android.gms:play-services-location:21.4.0"
    )
    // Yemek fotoğrafı önizleme (Photo Picker'dan seçilen content:// URI)
    implementation(
        "io.coil-kt.coil3:coil-compose:3.3.0"
    )
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
    implementation(
        libs.androidx.navigation.compose
    )

    implementation(
        libs.androidx.compose.material3
    )

    implementation(
        libs.androidx.compose.ui
    )

    implementation(
        libs.androidx.compose.ui.graphics
    )

    implementation(
        libs.androidx.compose.ui.tooling.preview
    )

    implementation(
        libs.androidx.core.ktx
    )

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    // REST API bağlantısı
    implementation(
        "com.squareup.retrofit2:retrofit:3.0.0"
    )

    // JSON verilerini Kotlin modellerine dönüştürür
    implementation(
        "com.squareup.retrofit2:converter-gson:3.0.0"
    )

    // API isteklerini ve cevaplarını Logcat'te gösterir
    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )
    // JWT ve kullanıcı oturum bilgilerini saklar
    implementation(
        "androidx.datastore:datastore-preferences:1.2.1"
    )

    testImplementation(
        libs.junit
    )

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
    // Compose ViewModel desteği
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0"
    )

// StateFlow verilerini Compose ekranlarında
// yaşam döngüsüne uygun takip etmek için
    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.10.0"
    )
}