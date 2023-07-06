package ge.dbenadshis.messengerapp.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.dbenadshis.messengerapp.model.User
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(){
    var curUser = User()
    suspend fun addUser(nickname: String, pass: String, work: String, callback: UserRepositoryImpl.ChildExistenceCallback){
        val repoImpl = userRepository as UserRepositoryImpl
        repoImpl.addUser(nickname, pass, work, callback)
    }
    suspend fun checkUser(nickname: String, pass: String, callback : UserRepositoryImpl.UserExistenceCallback){
        val repoImpl = userRepository as UserRepositoryImpl
        viewModelScope.launch {
            repoImpl.checkUser(nickname, pass, callback)
        }
    }
}