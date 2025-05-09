package com.se2gruppe5.risikofrontend.network.sse.messages

import com.se2gruppe5.risikofrontend.network.sse.IMessage

data class ChatMessage(val message: String) : IMessage
