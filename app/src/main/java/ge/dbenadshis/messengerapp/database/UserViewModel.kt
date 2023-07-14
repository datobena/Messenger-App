package ge.dbenadshis.messengerapp.database

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.dbenadshis.messengerapp.model.User

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    imageRepository: ImageRepository
) : ViewModel(){

    val userRepo = userRepository as UserRepositoryImpl
    private val imageRepo = imageRepository as ImageRepositoryImpl
    var curUser = User()


    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(true)
    val isSearching = _isSearching.asStateFlow()

    private  var _persons = MutableStateFlow(listOf<User>())
    var persons = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_persons) { text, persons ->
            if (text.length < 3) {
                persons
            } else {
                userRepo.searchUsersOnServer(text)
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _persons.value
    )
    fun onSearchTextChange(text: String){
        _searchText.value =  text
    }
    fun changeAvatar(imageUri: Uri){
        imageRepo.uploadImgAndSaveURL(imageUri, curUser.nickname)
    }

    suspend fun addUser(nickname: String, pass: String, work: String, callback: UserRepositoryImpl.ChildExistenceCallback){
        val repoImpl = userRepository as UserRepositoryImpl
        repoImpl.addUser(nickname, pass, work, callback)
    }
     fun setAllUsers(){
        val repoImpl = userRepository as UserRepositoryImpl
         viewModelScope.launch {
             repoImpl.getAllUsers(object : UserRepositoryImpl.OnUsersProvided {
                 override fun onUserExists(users: List<User>) {
                     _persons.value = users
                     _isSearching.update { false }
                 }

                 override fun onUserDoesNotExist() {

                 }
             })
         }
    }

    suspend fun checkUser(nickname: String, pass: String, callback : UserRepositoryImpl.UserExistenceCallback){
        val repoImpl = userRepository as UserRepositoryImpl
        viewModelScope.launch {
            repoImpl.checkUser(nickname, pass, callback)
        }
    }

    fun updateCurUser(nickname: String, work: String, uri: Uri?) {
        TODO("Not yet implemented")
    }
}