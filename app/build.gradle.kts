plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "seo.example.testproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "seo.example.testproject"
        minSdk = 31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.naver.maps:map-sdk:3.21.0")

    implementation ("androidx.fragment:fragment:1.6.2")
    // Fragment 사용 시 필수, 최신 버전 사용 권장
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    // Google Location Services API (현재 위치 가져오기 위해 필요)
    implementation ("com.google.android.gms:play-services-location:21.0.1") // 최신 버전 확인

    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.security:security-crypto:1.1.0-alpha06") // EncryptedSharedPreferences (선택 사항, 보안 강화)

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.android.volley:volley:1.2.1")

}