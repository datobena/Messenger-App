package ge.dbenadshis.messengerapp.database

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
    private val userRepository: UserRepository
) : ViewModel(){

    val repo = userRepository as UserRepositoryImpl

    var curUser = User()


    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(true)
    val isSearching = _isSearching.asStateFlow()

    private  var _persons = MutableStateFlow(listOf<User>())
    private var lastSearched = listOf<User>()
    var persons = searchText
        .debounce(1000L)
        .onEach { _isSearching.update { true } }
        .combine(_persons) { text, persons ->
            if (text.isEmpty()) {
                lastSearched = persons
                persons
            }else if(text.length < 3){
                lastSearched
            } else {
                lastSearched = repo.searchUsersOnServer(text)
                lastSearched
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
}