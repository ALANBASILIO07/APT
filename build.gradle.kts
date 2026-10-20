plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// OneDrive bloquea archivos de build/ mientras sincroniza (AccessDeniedException).
// Por eso los archivos de compilación van fuera de OneDrive: C:\Users\<tú>\.apt-build
allprojects {
    layout.buildDirectory.set(file("${System.getProperty("user.home")}/.apt-build/${project.name}"))
}
