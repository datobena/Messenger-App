package ge.dbenadshis.messengerapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.qualifiers.ApplicationContext
import ge.dbenadshis.messengerapp.model.Message

import javax.inject.Inject
import javax.inject.Named

class ChatRepositoryImpl @Inject constructor(
    @Named("messages")private val messages: DatabaseReference,
    @ApplicationContext applicationContext: Context
): ChatRepository {

    private val _messages = MutableLiveData<List<Message>>()
    val allMessages: LiveData<List<Message>> get() = _messages

    init{
        val sharedPreferences =
            applicationContext.getSharedPreferences("message-app", Context.MODE_PRIVATE)

        messages.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val currUser = sharedPreferences.getString("nickname","")
                val message = snapshot.getValue(Message::class.java) ?: return
                if (message.sender == currUser ||
                        message.receiver == currUser) {
//                        println(message.toString())
                        addMessageInList(message)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun addMessageInList(message: Message) {
        val updatedList = _messages.value.orEmpty().toMutableList().apply {
            add(message)
        }
        _messages.value = updatedList
    }
    override suspend fun addMessage(message: Message) {
        messages.push().setValue(message)
    }

    override suspend fun generateMessages(sender: String, receiver: String) {

    }
}