package com.se2gruppe5.risikofrontend

import com.se2gruppe5.risikofrontend.network.sse.SseClientService
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
        val HOST: String = dotenv["HOST"]
        val SSE_URL: String = dotenv["SSE_URL"]
        val SSE_URL_REJOIN: String = dotenv["SSE_URL_REJOIN"]
        val CHAT_SEND_URL: String = dotenv["CHAT_SEND_URL"]
        val LOBBY_CREATE_URL: String = dotenv["LOBBY_CREATE_URL"]
        val LOBBY_RESOURCE_URL: String = dotenv["LOBBY_RESOURCE_URL"]
        val LOBBY_PLAYER_URL: String = dotenv["LOBBY_PLAYER_URL"]
        val LOBBY_START_GAME_URL: String = dotenv["LOBBY_START_GAME_URL"]
        val UPDATE_PLAYER_URL: String = dotenv["UPDATE_PLAYER_URL"]
        val GET_GAME_INFO_URL: String = dotenv["GET_GAME_INFO_URL"]
        val CHANGE_PHASE_URL: String = dotenv["CHANGE_PHASE_URL"]
        val CHANGE_TERRITORY_URL: String = dotenv["CHANGE_TERRITORY_URL"]
        val GAME_MANAGER_MAX_PLAYERS: Int = Integer.valueOf(dotenv["MAX_PLAYERS"])
    }
}