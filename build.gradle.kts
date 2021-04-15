plugins {
    kotlin("jvm") version "1.4.32"
    maven
}

repositories {
    jcenter()
    maven ("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")
}

tasks {
    compileKotlin { kotlinOptions.jvmTarget = "1.8" }
    compileJava { options.encoding = "UTF-8" }
}