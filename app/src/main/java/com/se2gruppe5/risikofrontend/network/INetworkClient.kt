package com.se2gruppe5.risikofrontend.network

interface INetworkClient {
    suspend fun sendChat(message: String)
    suspend fun createLobby(): String?
    suspend fun deleteLobby(lobbyCode: String)
    suspend fun joinLobby(lobbyCode: String, playerName: String)
    suspend fun leaveLobby(lobbyCode: String)
    suspend fun startGame(lobbyCode: String)
    suspend fun changeTerritory(t1: TerritoryRecord, t2: TerritoryRecord)
    suspend fun changePhase()
    suspend fun changePlayers(players: List<PlayerRecord>)
    suspend fun cardAction(action: String, player: PlayerRecord, card: CardRecord)
}