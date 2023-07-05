package ge.dbenadshis.messengerapp.database

import ge.dbenadshis.messengerapp.model.User

interface UserRepository {
    suspend fun addUser(user: User)
}