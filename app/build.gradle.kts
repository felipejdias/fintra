import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    id("jacoco")
}

val keystoreProps = Properties().apply {
    val propsFile = rootProject.file("keystore.properties")
    if (propsFile.exists()) propsFile.inputStream().use(::load)
}

fun signingSecret(name: String): String? =
    System.getenv(name) ?: keystoreProps.getProperty(name)

android {
    namespace = "app.fintra"
    compileSdk = 35

    signingConfigs {
        create("release") {
            storeFile = signingSecret("RELEASE_STORE_FILE")?.let { rootProject.file(it) }
            storePassword = signingSecret("RELEASE_STORE_PASSWORD")
            keyAlias = signingSecret("RELEASE_KEY_ALIAS")
            keyPassword = signingSecret("RELEASE_KEY_PASSWORD")
        }
    }

    defaultConfig {
        applicationId = "app.fintra"
        minSdk = 26
        targetSdk = 35
        versionCode = (System.getenv("VERSION_CODE") ?: "1").toInt()
        versionName = System.getenv("VERSION_NAME") ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            signingConfig = if (signingSecret("RELEASE_STORE_FILE") != null)
                signingConfigs.getByName("release")
            else
                signingConfigs.getByName("debug")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}


tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

val jacocoTestReport by tasks.registering(JacocoReport::class) {
    // Agora depende de ambos os tipos de testes
    dependsOn("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*", "**/androidx/**/*.*",
        "**/*Dagger*.*", "**/*MembersInjector*.*", "**/*_Factory*.*",
        "**/*_ProvideField*.*", "**/*_ViewBinding*.*", "**/*_Impl*.*"
    )

    // Onde estão as classes compiladas do Kotlin
    val debugTree = fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    // Onde está o seu código fonte
    sourceDirectories.setFrom(files("${project.projectDir}/src/main/java", "${project.projectDir}/src/main/kotlin"))
    classDirectories.setFrom(files(debugTree))

    // AQUI ESTÁ O SEGREDO: Unir os dados de execução dos dois testes
    executionData.setFrom(fileTree("${project.layout.buildDirectory.get()}") {
        include(
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec", // Unitários
            "outputs/code_coverage/debugAndroidTest/connected/*/*.ec" // UI/AndroidTest
        )
    })
}

tasks.register("buildAndCheckSafe") {
    group = "Custom"
    description = "Roda todos os testes, gera relatório de cobertura e cria o APK de Release se tudo estiver OK."

    // Define a ordem: 1. Testes e Cobertura -> 2. Build do APK
    dependsOn(jacocoTestReport)
    finalizedBy("assembleRelease")

    doLast {
        println("✅ Todos os testes passaram e o relatório de cobertura foi gerado!")
        println("📦 Gerando APK de Release...")
    }
}
