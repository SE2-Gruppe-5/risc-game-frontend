package com.se2gruppe5.risikofrontend

import io.github.cdimascio.dotenv.dotenv


class Constants {
    companion object {
        // Env directory path is different running unit tests, since Android bundles "assets" in the project root
        // The path for unit tests is configured in the Gradle build file
        private val env_path = System.getProperty("env_dir") ?: "/assets"

        private val dotenv = dotenv {
            directory = env_path
            filename = "env"
        }
        val SSE_URL: String = dotenv["SSE_URL"]
        val CHAT_URL: String = dotenv["CHAT_URL"]
    }
}