package ge.dbenadshis.messengerapp.database

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.dbenadshis.messengerapp.chatViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): ViewModel() {

}