plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.devvikram.chatmate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.devvikram.chatmate"
        minSdk = 21
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    ksp{
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.support.annotations)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.ui.desktop)
    implementation(libs.androidx.recyclerview)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)

    implementation(libs.firebase.bom)

    implementation("com.ncorti:slidetoact:0.11.0")
    implementation(libs.circleimageview)
    implementation("com.android.volley:volley:1.2.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.room.ktx)

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // gson converter
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")


    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)

    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.otaliastudios:cameraview:2.7.2")

    implementation("com.google.firebase:firebase-storage")


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}