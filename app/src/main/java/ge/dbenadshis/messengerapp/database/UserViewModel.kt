package ge.dbenadshis.messengerapp.database

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.dbenadshis.messengerapp.model.User
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(){
    suspend fun addUser(user: User){
        val repoImpl = userRepository as UserRepositoryImpl
        repoImpl.addUser(user)
    }
}