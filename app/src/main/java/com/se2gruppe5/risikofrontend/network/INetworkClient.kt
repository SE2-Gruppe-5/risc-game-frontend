package com.se2gruppe5.risikofrontend.network

import com.se2gruppe5.risikofrontend.game.dataclasses.CardRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.PlayerRecord
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import java.util.UUID

interface INetworkClient {
    suspend fun sendChat(message: String)
    suspend fun createLobby(): String?
    suspend fun deleteLobby(lobbyCode: String)
    suspend fun joinLobby(lobbyCode: String, playerName: String)
    suspend fun leaveLobby(lobbyCode: String)
    suspend fun startGame(lobbyCode: String)
    suspend fun updatePlayer(gameId: UUID, playerId: UUID, name: String, color: Int)
    suspend fun getGameInfo(gameId: UUID)
    suspend fun changePhase(gameId: UUID)
    suspend fun changeTerritory(gameId: UUID, territory: TerritoryRecord)
    suspend fun cardAction(gameId: UUID, action: String, player: PlayerRecord, card: CardRecord)
    suspend fun assignTerritories(gameId: UUID)
    suspend fun distributeTroops(gameId: UUID, troops: Int)
}