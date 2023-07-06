package ge.dbenadshis.messengerapp.database

import ge.dbenadshis.messengerapp.model.Message

interface ChatRepository {

    suspend fun addMessage(message: Message)

    suspend fun generateMessages(sender:String, receiver: String)

}