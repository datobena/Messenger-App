package ge.dbenadshis.messengerapp.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.dbenadshis.messengerapp.model.Message
import ge.dbenadshis.messengerapp.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    chatRepository: ChatRepository
): ViewModel() {
    var currentChatFriend: User = User()
    private val repo : ChatRepositoryImpl = chatRepository as ChatRepositoryImpl
    var allMessages = repo.allMessages

    private val _currentFriend = MutableStateFlow("")
    val currentFriend = _currentFriend.asStateFlow()

    fun getCurrentFriend(): String{
        return currentFriend.value
    }
    fun setListeners(sender: String){
        viewModelScope.launch {
            repo.setListener(sender)
        }
    }
    fun clearNickname(){
        _currentFriend.update { "" }
    }
    fun reset(){
        repo.reset()
    }
    fun getNickname(name:String){
        _currentFriend.update { "" }
        viewModelScope.launch {
            repo.getNickname(name, object : ChatRepositoryImpl.OnNicknameExist{
                override fun OnExist(key: String) {
                    _currentFriend.update { key }
                }
                override fun OnDoesNotExist() {
                }

            })
        }
    }

    suspend fun addMessage(message: Message){
        repo.addMessage(message)
    }
}