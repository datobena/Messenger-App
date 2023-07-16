package ge.dbenadshis.messengerapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import ge.dbenadshis.messengerapp.model.Message

import kotlinx.coroutines.tasks.await

import javax.inject.Inject
import javax.inject.Named

class ChatRepositoryImpl @Inject constructor(
    @Named("messages") private val messages: DatabaseReference
) : ChatRepository {

    private val _messages = MutableLiveData<List<Message>>()
    val allMessages: LiveData<List<Message>> get() = _messages

    private var isListenerSet : HashMap<String, Boolean> =  HashMap()

    fun reset(){
        _messages.postValue(listOf())
    }
    suspend fun setListener(sender: String) {
        val senderKey = messages.child("nicknames").child(sender).get().await().value as String
        val temp = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java) ?: return
                addMessageInList(message)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        if(!isListenerSet.containsKey(senderKey)) {
            messages.child(senderKey).child("messages")
                .addChildEventListener(temp)
            isListenerSet[senderKey] = true
        }else{
            generateMessages(senderKey)
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

    override suspend fun generateMessages(senderKey: String) {
        val temp = messages.child(senderKey).child("messages").get().await().getValue<HashMap<String, Message>>()
        var res = mutableListOf<Message>()
        if(temp != null){
            res = temp.values.toMutableList()
        }
        res.sortBy { it.date }
        _messages.postValue(res)
    }

    suspend fun getNickname(name: String, callback: OnNicknameExist){
        val res = messages.child("nicknames").child(name).get().await()
            .getValue(String::class.java)
        if(res != null) {
            callback.OnExist(res)
        }else{
            callback.OnDoesNotExist()
        }
    }

    interface OnNicknameExist {
        fun OnExist(key: String)
        fun OnDoesNotExist()
    }
}