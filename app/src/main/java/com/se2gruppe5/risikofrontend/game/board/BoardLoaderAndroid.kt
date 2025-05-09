package com.se2gruppe5.risikofrontend.game.board

import android.content.Context
import com.se2gruppe5.risikofrontend.game.dataclasses.TerritoryRecord
import java.io.BufferedReader
import java.io.InputStreamReader

class BoardLoaderAndroid(context: Context, assetPath: String = "board.json") {
    val boardLoader: BoardLoader = BoardLoader(readFile(assetPath, context))

    fun getTerritories(): List<TerritoryRecord> {
        return boardLoader.getTerritories()
    }

    private fun readFile(assetPath: String, context: Context): String {
        val builder = StringBuilder()

        val reader = BufferedReader(InputStreamReader(context.assets.open(assetPath)))

        var line: String? = reader.readLine()
        while(line != null) {
            builder.append(line).append("\n")
            line = reader.readLine()
        }

        reader.close()

        return builder.toString()
    }
}