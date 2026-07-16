plugins {
  application
  java
  id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
  implementation("org.jmdns:jmdns:3.6.3")
}

javafx {
  version = "17"
  modules("javafx.controls", "javafx.fxml")
}

application {
  mainClass.set("vertexlink.App")
  applicationName = "VertexLink"
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks.test {
  useJUnitPlatform()
}
