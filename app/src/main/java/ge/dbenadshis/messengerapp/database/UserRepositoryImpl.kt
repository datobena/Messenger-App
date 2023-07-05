package ge.dbenadshis.messengerapp.database

import com.google.firebase.database.DatabaseReference
import ge.dbenadshis.messengerapp.model.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseApp: DatabaseReference
) : UserRepository{
    override suspend fun addUser(user: User) {
        firebaseApp.push().setValue(user)
    }
}