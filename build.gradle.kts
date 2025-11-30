// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    androidApplication version Version.agp apply false
    kotlinJvm version Version.Kotlin.language apply false
    kotlinAndroid version Version.Kotlin.language apply false
    composeCompiler version Version.Kotlin.language apply false
}
/*
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Ktor
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-okhttp:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}
 */