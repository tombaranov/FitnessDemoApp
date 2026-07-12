plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "tombaranov.fitnessdemoapp.player.impl"
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
    implementation(project(":feature:player:api"))
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.core.ktx)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutines.core)
}
