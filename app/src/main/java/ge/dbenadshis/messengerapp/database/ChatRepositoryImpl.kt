package ge.dbenadshis.messengerapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.CancellationTokenSource

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.qualifiers.ApplicationContext
import ge.dbenadshis.messengerapp.model.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import javax.inject.Inject
import javax.inject.Named

class ChatRepositoryImpl @Inject constructor(
    @Named("messages") private val messages: DatabaseReference,
    @ApplicationContext applicationContext: Context
) : ChatRepository {

    private val _messages = MutableLiveData<List<Message>>()
    val allMessages: LiveData<List<Message>> get() = _messages

    init {
        val sharedPreferences =
            applicationContext.getSharedPreferences("message-app", Context.MODE_PRIVATE)
        val currUser = sharedPreferences.getString("nickname", "")!!
        if (currUser != "") {
            CoroutineScope(Dispatchers.IO).launch {
                val key = messages.child("nicknames").child(currUser).get().await()
                    .getValue(String::class.java)
                messages.child(key!!).child("messages")
                    .addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            val message = snapshot.getValue(Message::class.java) ?: return
                            addMessageInList(message)
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            // Handle child changed event
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                            // Handle child removed event
                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            // Handle child moved event
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle cancellation
                        }
                    })
            }
        }
    }

    private fun addMessageInList(message: Message) {
        val updatedList = _messages.value.orEmpty().toMutableList().apply {
            add(message)
        }
        _messages.value = updatedList
    }

    override suspend fun addMessage(message: Message) {
        val keyReceiver = messages.child("nicknames").child(message.receiver).get().await()
            .getValue(String::class.java)
        val keySender = messages.child("nicknames").child(message.sender).get().await()
            .getValue(String::class.java)
        message.sender = keySender!!
        message.receiver = keyReceiver!!
        messages.child(keyReceiver).child("messages").push().setValue(message)
        message.isSentByCurrentUser = true
        messages.child(keySender).child("messages").push().setValue(message)
    }

    override suspend fun generateMessages(sender: String, receiver: String) {

    }
}