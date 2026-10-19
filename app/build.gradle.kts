import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

val secretsProperties = Properties().apply {
    val f = rootProject.file("secrets.properties")
    if (!f.exists()) {
        throw GradleException(
            "secrets.properties is missing. Create it in the project root with " +
                "SUPABASE_URL and SUPABASE_ANON_KEY entries."
        )
    }
    f.inputStream().use { load(it) }
}

val keystoreProperties = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "lk.hiresmcq.packcalc"
    compileSdk = 35

    defaultConfig {
        applicationId = "lk.hiresmcq.packcalc"
        minSdk = 26
        targetSdk = 35
        versionCode = 15
        versionName = "1.6.1"
        vectorDrawables { useSupportLibrary = true }

        val supabaseUrl = secretsProperties.getProperty("SUPABASE_URL")
            ?: throw GradleException("SUPABASE_URL missing from secrets.properties")
        val supabaseAnonKey = secretsProperties.getProperty("SUPABASE_ANON_KEY")
            ?: throw GradleException("SUPABASE_ANON_KEY missing from secrets.properties")
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
    }

    signingConfigs {
        if (keystoreProperties.containsKey("storeFile")) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
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
        compose = true
        buildConfig = true
    }
}

// Never produce a release build without the published signing key: a silently
// debug-signed (or unsigned) APK cannot be installed as an update over a
// release-signed one. Fail loudly instead.
gradle.taskGraph.whenReady {
    val buildsRelease = allTasks.any { it.name.contains("Release") }
    if (buildsRelease && !keystoreProperties.containsKey("storeFile")) {
        throw GradleException(
            "Release signing is not configured. Create keystore.properties with storeFile, " +
                "storePassword, keyAlias and keyPassword so release builds use the same key " +
                "as published releases."
        )
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
}
