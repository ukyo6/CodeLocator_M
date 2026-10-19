plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
    id("org.jetbrains.intellij") version "1.6.0"
}

group = "com.bytedance.tools"
version = "2.0.6"

repositories {
    mavenCentral()
    google()
}

// 新版 AS 不再附带旧布局解析器, 仅打包编译 SDK 中该独立包的类以保留无 SDK 抓取能力.
val legacyLayoutInspector by tasks.registering(Jar::class) {
    archiveBaseName.set("codelocator-layoutinspector")
    archiveVersion.set("2022.1")
    from(provider {
        zipTree(configurations.compileClasspath.get().single { it.name == "sdk-tools.jar" })
    }) {
        include("com/android/layoutinspector/**")
    }
    from("../LICENSE.txt") {
        into("META-INF")
        rename { "LICENSE.txt" }
    }
    from("src/main/resources/META-INF/NOTICE-layoutinspector.txt") {
        into("META-INF")
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("com.hankcs:hanlp:portable-1.8.6")
    implementation("javazoom:jlayer:1.0.1")
    implementation(project(":CodeLocatorModel"))
    implementation("io.reactivex.rxjava3:rxjava:3.1.7")
    implementation("com.google.zxing:core:3.5.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    runtimeOnly(files(legacyLayoutInspector))
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1")
    type.set("IC") // Target IDE Platform
    // UI 均由代码构建, 无 .form 文件; 避免旧 javac2 在 macOS 上错误扫描 JDK 的 Packages 目录.
    instrumentCode.set(false)

    plugins.set(listOf("com.intellij.java", "org.jetbrains.kotlin", "android", "git4idea"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("262.*")
    }
}

tasks.named<org.jetbrains.intellij.tasks.PrepareSandboxTask>("prepareSandbox") {
    from(listOf("imgcopy.m", "JarModuleTemplate.zip", "AndroidModuleTemplate.zip", "codelocatorhelper.apk", "restartAndroidStudio")) {
        into(rootProject.name)
    }
}
