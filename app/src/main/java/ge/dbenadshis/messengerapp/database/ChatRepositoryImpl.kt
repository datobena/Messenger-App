package ge.dbenadshis.messengerapp.database

import com.google.firebase.database.DatabaseReference
import ge.dbenadshis.messengerapp.model.Message
import javax.inject.Inject
import javax.inject.Named

class ChatRepositoryImpl @Inject constructor(
    @Named("messages")private val messages: DatabaseReference
): ChatRepository {
    override suspend fun addMessage(message: Message) {

    }
    override suspend fun generateMessages(sender: String, receiver: String) {
        TODO("Not yet implemented")
    }
}