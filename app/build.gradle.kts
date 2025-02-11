plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.setwallpaper"
    compileSdk = 35

    viewBinding{
        enable = true
    }

    defaultConfig {
        applicationId = "com.example.setwallpaper"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.squareup.okhttp3","okhttp","4.9.3")
    implementation("com.squareup.okhttp3","logging-interceptor","4.9.3")
    implementation("com.google.code.gson","gson","2.10.1")


    implementation("io.coil-kt","coil","2.4.0")
    implementation("net.lingala.zip4j","zip4j","2.11.5")

    // Coroutines
    implementation ("org.jetbrains.kotlinx","kotlinx-coroutines-core","1.7.1")
    implementation ("org.jetbrains.kotlinx","kotlinx-coroutines-android","1.7.1")

    // Coroutine Lifecycle Scopes
    implementation ("androidx.lifecycle","lifecycle-runtime-ktx","2.6.2")


}