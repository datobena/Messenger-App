package ge.dbenadshis.messengerapp.database



interface UserRepository {
    suspend fun addUser(nickname: String, pass: String, work: String, callback: UserRepositoryImpl.ChildExistenceCallback)

    suspend fun checkUser(nickname: String, pass: String, callback: UserRepositoryImpl.UserExistenceCallback)

    suspend fun getAllUsers(callback: UserRepositoryImpl.OnUsersProvided)
}