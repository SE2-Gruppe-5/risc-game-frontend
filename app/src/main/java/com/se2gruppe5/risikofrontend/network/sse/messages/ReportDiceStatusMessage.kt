package com.se2gruppe5.risikofrontend.network.sse.messages

import com.se2gruppe5.risikofrontend.network.sse.IMessage

data class ReportDiceStatusMessage(val results: List<Int>) : IMessage {}
