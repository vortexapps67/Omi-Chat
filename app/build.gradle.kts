plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Load .env file for local development
fun loadEnvFile(): Map<String, String> {
    val envFile = file(".env")
    val envMap = mutableMapOf<String, String>()
    if (envFile.exists()) {
        envFile.readText().split("\n").forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                val parts = trimmed.split("=", limit = 2)
                if (parts.size == 2) {
                    envMap[parts[0].trim()] = parts[1].trim()
                }
            }
        }
    }
    return envMap
}

val envConfig = loadEnvFile()

fun getEnv(key: String): String {
    return envConfig[key] ?: System.getenv(key) ?: ""
}

android {
    namespace = "com.popchat"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.popchat"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // Build config fields from environment
        buildConfigField("String", "SUPABASE_URL", "\"${getEnv("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"${getEnv("SUPABASE_PUBLISHABLE_KEY")}\"")
        buildConfigField("String", "SUPABASE_SECRET_KEY", "\"${getEnv("SUPABASE_SECRET_KEY")}\"")
        buildConfigField("String", "SUPABASE_JWKS_URL", "\"${getEnv("SUPABASE_JWKS_URL")}\"")
    }

    // Declared before buildTypes so the release build type can reference it.
    signingConfigs {
        create("release") {
            // Resolved against the repository root, not this module's directory.
            // CI writes the decoded keystore to <repo>/keystore/release.keystore
            // and sets KEYSTORE_PATH=keystore/release.keystore, but a bare
            // file() inside app/build.gradle.kts resolves relative to app/ and
            // would look for app/keystore/release.keystore - which never exists.
            // The path would then silently fall through to an unsigned APK, so
            // the release would build but never be installable.
            val storePath = System.getenv("KEYSTORE_PATH")
            if (!storePath.isNullOrBlank()) {
                val candidate = rootProject.file(storePath)
                if (candidate.exists()) {
                    storeFile = candidate
                    storePassword = System.getenv("KEYSTORE_PASSWORD")
                    keyAlias = System.getenv("KEY_ALIAS")
                    keyPassword = System.getenv("KEY_PASSWORD")
                } else {
                    logger.lifecycle(
                        "OmiChat: KEYSTORE_PATH points at ${candidate.absolutePath}, which does not exist. " +
                            "Falling back to an unsigned release APK."
                    )
                }
            } else {
                logger.lifecycle(
                    "OmiChat: KEYSTORE_PATH is unset. Falling back to an unsigned release APK."
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Assigning a signing config with no storeFile throws at configuration
            // time, which would break every build on a machine that has not been
            // given release credentials. Unsigned output is the better default.
            if (signingConfigs.getByName("release").storeFile != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Kotlin 2.0 renamed -Xopt-in to -opt-in; the old spelling still works
        // but is deprecated and warns on every compile.
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.runtime.ExperimentalComposeApi",
            // TopAppBar and the other M3 app-bar surfaces are still marked
            // experimental on the Material3 1.2.1 line this app compiles
            // against. Opting in project-wide is deliberate: every screen that
            // uses one would otherwise need its own @OptIn annotation, which
            // says nothing about whether the app handles the API correctly.
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            // HorizontalPager, used by the onboarding carousel.
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            // Blurred glass edges (Modifier.blur with a BlurredEdgeTreatment).
            "-opt-in=androidx.compose.ui.graphics.ExperimentalGraphicsApi"
        )
    }

    buildFeatures {
        compose = true
        // Required because defaultConfig declares custom buildConfigField values
        // (the Supabase keys); the feature is off by default since AGP 8.
        buildConfig = true
    }
    // viewBinding and dataBinding are intentionally off: the app is Compose
    // only and has no res/layout directory, so both compiler passes would be
    // pure overhead.

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,license.txt,NOTICE}"
        }
    }

    // Feature flags for new Android Gradle Plugin features
    experimentalProperties["android.experimental.r8.desugaring"] = "true"
}

dependencies {
    // Android & Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // collectAsStateWithLifecycle()
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Compose BOM & Libraries
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // HorizontalPager/rememberPagerState come from androidx.compose.foundation:
    // foundation on the Compose 1.6.x line; there is no standalone pager
    // artifact to declare.

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt DI
    // The Gradle plugin fails configuration outright if hilt-android itself is
    // absent, so the runtime is required alongside the compiler.
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.androidx.hilt.compiler)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coroutines & Serialization
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // Network
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)

    // Image Loading. Coil 2.x includes its OkHttp fetcher in the core artifact.
    implementation(libs.coil.compose)

    // Supabase
    implementation(libs.supabase.kt)
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.storage)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)

    // Paging
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime.ktx)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.video)

    // Biometric & Security
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.security.crypto)

    // Logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.manifest)
}

ksp {
    // Mirrors the old kapt `correctErrorTypes` behaviour for Room's generated
    // DAO/database bindings.
    arg("room.generateKotlin", "true")
    arg("room.incremental", "true")
    arg("room.schemaLocation", "$projectDir/schemas")
}

// Compiler opt-ins live in the android { kotlinOptions { } } block above. This
// used to be duplicated as a top-level tasks.withType<KotlinCompile> block,
// which could not compile: KotlinCompile is not resolvable from a build script
// classpath without an explicit import, and the `kotlinOptions` extension it
// would call is scoped to the Android extension, not to the task.
