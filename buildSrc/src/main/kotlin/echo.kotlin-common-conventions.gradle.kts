plugins {
  id("org.jetbrains.kotlin.jvm")
}

repositories {
  mavenCentral()
}

dependencies {
  constraints {
    implementation(kotlin("stdlib-jdk8"))
  }

  implementation(platform(kotlin("bom")))
  implementation(kotlin("stdlib-jdk8"))

  testImplementation(kotlin("test"))
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}
