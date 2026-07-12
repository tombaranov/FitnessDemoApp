plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "tombaranov.fitnessdemoapp.player.api"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(libs.androidx.media3.ui)
    api(libs.kotlinx.coroutines.core)
}
