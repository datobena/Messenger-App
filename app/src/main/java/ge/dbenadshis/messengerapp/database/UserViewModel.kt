package ge.dbenadshis.messengerapp.database

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.dbenadshis.messengerapp.MissingFields
import ge.dbenadshis.messengerapp.TransactionState
import ge.dbenadshis.messengerapp.database.UserRepositoryImpl.ChildExistenceCallback
import ge.dbenadshis.messengerapp.model.User
import ge.dbenadshis.messengerapp.sharedPreferences

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
    val imageRepo = imageRepository as ImageRepositoryImpl
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

    suspend fun addUser(nickname: String, pass: String, work: String, callback: ChildExistenceCallback){
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

    fun updateCurUser(
        nickname: String,
        work: String,
        uri: Uri?,
        result: MutableState<TransactionState>,
        mutMissingField: MutableState<MissingFields>
    ) {
        when(""){
            nickname -> mutMissingField.value = MissingFields.NICKNAME
            work -> mutMissingField.value = MissingFields.WORK
            else -> {
                mutMissingField.value = MissingFields.NONE
                if(nickname == curUser.nickname && work == curUser.work && (uri?.toString() ?: "") == curUser.avatarURL) {
                    result.value = TransactionState.FINISHED
                    return
                }
                result.value = TransactionState.LOADING
                val repoImpl = userRepository as UserRepositoryImpl
                val lastNickname = curUser.nickname
                repoImpl.nicknameExists(nickname, object : ChildExistenceCallback{
                    override fun onChildExists(dataSnapshot: DataSnapshot) {
                        if(nickname == lastNickname) {
                            onChildDoesNotExist(dataSnapshot)
                            return
                        }
                        Log.d("updateUserErr", "User with given name already exists")
                        result.value = TransactionState.FINISHED_EXISTS
                    }

                    override fun onChildDoesNotExist(dataSnapshot: DataSnapshot) {
                        val key = dataSnapshot.child("nicknames").child(lastNickname).getValue(String::class.java)
                        val userRef = dataSnapshot.child(key!!).ref
                        val nicknamesRef = dataSnapshot.child("nicknames").ref
                        nicknamesRef.child(lastNickname).removeValue()
                        nicknamesRef.ref.child(nickname).setValue(key)
                        userRef.child("nickname").setValue(nickname)
                        userRef.child("work").setValue(work)
                        if(uri != null && uri.toString() != curUser.avatarURL)
                            imageRepo.uploadImgAndSaveURL(uri, key, result)
                        else
                            result.value = TransactionState.FINISHED
                        sharedPreferences!!.edit().putString("nickname", nickname).apply()

                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val updatedUser = dataSnapshot.getValue(User::class.java)
                                curUser = updatedUser!!

                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d("updateUserErr", "Request to database has been canceled! ${databaseError.message}")
                            }
                        })
                    }

                })
            }
        }
    }
}