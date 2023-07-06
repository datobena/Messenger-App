package ge.dbenadshis.messengerapp.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import ge.dbenadshis.messengerapp.model.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseApp: DatabaseReference
) : UserRepository{
    override suspend fun addUser(nickname: String, pass: String, work: String, callback: ChildExistenceCallback) {
        nicknameExists(nickname, object : ChildExistenceCallback{
            override fun onChildExists(dataSnapshot: DataSnapshot) {
                callback.onChildExists(dataSnapshot)
            }

            override fun onChildDoesNotExist() {
                firebaseApp.child(nickname).setValue(User(nickname, PasswordUtils.hashPassword(pass), work))
                callback.onChildDoesNotExist()
            }

        })
    }

    override suspend fun checkUser(nickname: String, pass: String, callback: UserExistenceCallback) {
        nicknameExists(nickname, object : ChildExistenceCallback {
            override fun onChildExists(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.child(nickname).getValue<User>()
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

    fun nicknameExists(nickname: String, callback: ChildExistenceCallback){
        firebaseApp.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(nickname)) {
                    callback.onChildExists(dataSnapshot)
                } else {
                    callback.onChildDoesNotExist()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle potential error
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
}