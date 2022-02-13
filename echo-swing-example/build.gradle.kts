plugins {
  id("echo.kotlin-application-conventions")
}

dependencies {
  implementation(project(":echo-core"))
  implementation(project(":echo-swing"))
}

application {
  mainClass.set("io.github.andrewbgm.echo.swing.example.SwingExampleKt")
}
