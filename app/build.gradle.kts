plugins { id("com.android.application") }

android {
    namespace = "cl.tsnchile.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "cl.tsnchile.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("production") {
            val keystorePath = System.getenv("TSN_KEYSTORE_PATH")
            val storePass = System.getenv("TSN_STORE_PASSWORD")
            val alias = System.getenv("TSN_KEY_ALIAS")
            val keyPass = System.getenv("TSN_KEY_PASSWORD")
            if (!keystorePath.isNullOrBlank() && !storePass.isNullOrBlank() &&
                !alias.isNullOrBlank() && !keyPass.isNullOrBlank()) {
                storeFile = file(keystorePath)
                storePassword = storePass
                keyAlias = alias
                keyPassword = keyPass
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("production")
        }
    }
}
