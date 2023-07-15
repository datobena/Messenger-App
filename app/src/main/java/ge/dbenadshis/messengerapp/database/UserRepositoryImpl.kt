package ge.dbenadshis.messengerapp.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import ge.dbenadshis.messengerapp.model.*

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class UserRepositoryImpl @Inject constructor(
    @Named("users") private val firebaseApp: DatabaseReference
) : UserRepository{

    override suspend fun addUser(nickname: String, pass: String, work: String, callback: ChildExistenceCallback) {
        nicknameExists(nickname, object : ChildExistenceCallback{
            override fun onChildExists(dataSnapshot: DataSnapshot) {
                callback.onChildExists(dataSnapshot)
            }

            override fun onChildDoesNotExist() {
                val key = firebaseApp.push().key
                firebaseApp.child(key!!).setValue(User(nickname, PasswordUtils.hashPassword(pass), work))
                firebaseApp.child("nicknames").child(nickname).setValue(key)
                callback.onChildDoesNotExist()
            }
        })
    }

    override suspend fun checkUser(nickname: String, pass: String, callback: UserExistenceCallback) {
        nicknameExists(nickname, object : ChildExistenceCallback {
            override fun onChildExists(dataSnapshot: DataSnapshot) {
                val key = dataSnapshot.child("nicknames").child(nickname).getValue<String>()
                println(key)
                val user = dataSnapshot.child(key!!).getValue<User>()
                if (user != null && PasswordUtils.verifyPassword(pass, user.passHash)) {
                    callback.onUserExists(user)
                } else {
                    callback.onUserDoesNotExist()
                }
            }

            override fun onChildDoesNotExist() {
                callback.onUserDoesNotExist()
            }
        })
    }

    override suspend fun getAllUsers(callback: OnUsersProvided) {
        firebaseApp.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = snapshot.children.mapNotNull { childSnapshot ->
                    if(childSnapshot.key != "nicknames")
                        childSnapshot.getValue(User::class.java)
                    else
                       null
                }
                callback.onUserExists(userList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("getAllUsersErr", "Request was cancelled! ${error.message}")

            }
        })
    }
    suspend fun searchUsersOnServer(query: String): List<User> = withContext(Dispatchers.IO) {
        val snapshot = firebaseApp
            .child("nicknames")
            .orderByKey()
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()
        val filteredUsers = snapshot.children.mapNotNull { childSnapshot ->
            val key = childSnapshot.getValue<String>()
            println(key)
            firebaseApp.child(key!!).get().await().getValue<User>()
        }
        println(filteredUsers.toString())
        return@withContext filteredUsers
    }

    fun nicknameExists(nickname: String, callback: ChildExistenceCallback){
        firebaseApp.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("nicknames").hasChild(nickname)) {
                    callback.onChildExists(dataSnapshot)
                } else {
                    callback.onChildDoesNotExist()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("nicknameExistsErr", "Request was cancelled! ${databaseError.message}")

            }
        })
    }
    interface ChildExistenceCallback {
        fun onChildExists(dataSnapshot: DataSnapshot)
        fun onChildDoesNotExist()
    }
    interface UserExistenceCallback {
        fun onUserExists(user: User)
        fun onUserDoesNotExist()
    }

    interface OnUsersProvided {
        fun onUserExists(users: List<User>)
        fun onUserDoesNotExist()
    }
}