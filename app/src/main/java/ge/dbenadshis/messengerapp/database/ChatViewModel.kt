package ge.dbenadshis.messengerapp.database

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.dbenadshis.messengerapp.model.Message
import ge.dbenadshis.messengerapp.model.User
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel() {
    var currentChatFriend: User = User()
    private val repo : ChatRepositoryImpl = chatRepository as ChatRepositoryImpl
    var allMessages = repo.allMessages

    suspend fun addMessage(message: Message){
        repo.addMessage(message)
    }
}