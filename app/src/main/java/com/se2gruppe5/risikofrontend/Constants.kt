package com.se2gruppe5.risikofrontend

import io.github.cdimascio.dotenv.dotenv
import java.io.File


class Constants {
    companion object {
        val projectRoot = File("").absolutePath
        val assetsPath = File(projectRoot, "assets").absolutePath
        val dotenv = dotenv{
            directory = assetsPath
            filename = "env"
        }
        val SSE_URL: String = dotenv["SSE_URL"]
        val CHAT_URL: String = dotenv["CHAT_URL"]
    }
}