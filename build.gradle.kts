// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kapt) apply false
    `maven-publish`
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            publishing {
                publications {
                    register<MavenPublication>("release") {
                        groupId = rootProject.property("lib.publish.group") as String
                        artifactId = property("artifactId") as String
                        version = rootProject.property("lib.publish.version") as String

                        println("publish library: ${project.name} groupId = $groupId artifactId = $artifactId version = $version")

                        afterEvaluate {
                            from(components["release"])
                        }
                    }
                }
                // test publish local
                repositories {
                    maven {
                        name = "CustomRepo"
                        url = uri(rootProject.layout.buildDirectory.dir("repo"))
                    }
                }
            }
        }
    }
}