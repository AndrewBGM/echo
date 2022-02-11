plugins {
  id("echo.kotlin-application-conventions")
}

dependencies {
  api(project(":echo-core"))
  api(project(":echo-swing"))
}

application {
  mainClass.set("io.github.andrewbgm.echo.swing.example.SwingExampleKt")
}
