plugins {
  application
  java
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

application {
  mainClass.set("vertexlink.App")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks.test {
  useJUnitPlatform()
}
