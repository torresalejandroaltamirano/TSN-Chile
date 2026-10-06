plugins { id("com.android.application") }

android {
    namespace = "cl.tsnchile.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "cl.tsnchile.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("TSN_KEYSTORE_PATH")
            if (!keystorePath.isNullOrBlank()) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("TSN_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("TSN_KEY_ALIAS")
                keyPassword = System.getenv("TSN_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("release") {
            if (!System.getenv("TSN_KEYSTORE_PATH").isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
        }
    }
}
