import io.github.krakowski.jextract.JextractTask

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(hexalite.plugins.jextract)
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    api(rootProject.hexalite.bundles.kotlin.essential)
    api(project(":common-kotlin"))
}

tasks {
    withType<JextractTask> {
        dependsOn(":native:cbindgen")
        header("${rootProject.projectDir.absolutePath}/target/release/client.h") {
            libraries.set(listOf("grpc_server_bindings"))
            targetPackage.set("org.hexalite.network.panama.grpc.client")
            className.set("GrpcClient")
            sourceMode.set(true)
        }
    }
    build {
        dependsOn("shadowJar")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.hexalite.network.grpc.client.HexaliteGrpcClientKt"
    }
}