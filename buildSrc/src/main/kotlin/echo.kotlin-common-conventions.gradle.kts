plugins {
  id("org.jetbrains.kotlin.jvm")
}

repositories {
  mavenCentral()
}

version = "0.0.1"

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

  withSourcesJar()
  withJavadocJar()
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

tasks.named<Jar>("jar") {
  manifest {
    attributes(mapOf(
      "Implementation-Title" to project.name,
      "Implementation-Version" to project.version))
  }
}
