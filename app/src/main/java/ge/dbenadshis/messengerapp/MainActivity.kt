package ge.dbenadshis.messengerapp


import ChatScreen
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import dagger.hilt.android.AndroidEntryPoint
import ge.dbenadshis.messengerapp.database.ChatViewModel
import ge.dbenadshis.messengerapp.database.UserRepositoryImpl
import ge.dbenadshis.messengerapp.database.UserViewModel
import ge.dbenadshis.messengerapp.model.User
import ge.dbenadshis.messengerapp.ui.theme.MessengerAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Search : Screen("search")
    object SignUp : Screen("signup")

    object Start : Screen("start")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Chat : Screen("chat")
}

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController, startDestination = Screen.Start.route) {
            composable(Screen.Login.route) {
                LogInPage()
            }
            composable(Screen.SignUp.route) {
                SignUpScreen()
            }
            composable(Screen.Home.route) {
                HomePage()
            }
            composable(Screen.Profile.route) {
                ProfilePage()
            }
            composable(Screen.Start.route) {
                StartScreen()
            }
            composable(Screen.Search.route) {
                SearchScreen()
            }
            composable(Screen.Chat.route) {
                ChatScreen(userViewModel.curUser.nickname, chatViewModel.currentChatFriend.nickname)
            }
        }
    }
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController found!")
}
lateinit var userViewModel: UserViewModel
lateinit var chatViewModel: ChatViewModel
var sharedPreferences: SharedPreferences? = null

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]


        sharedPreferences =
            applicationContext.getSharedPreferences("message-app", Context.MODE_PRIVATE)
        setContent {
            MessengerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    SetupNavGraph()
                }

            }
        }
    }

}

@Composable
fun SignUpScreen() {
    val navController = LocalNavController.current
    var mutNickname by remember {
        mutableStateOf("")
    }
    var mutPass by remember {
        mutableStateOf("")
    }
    var mutWork by remember {
        mutableStateOf("")
    }
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_image_placeholder),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.6f) // Set the desired width as a fraction of the available width
                        .aspectRatio(1f) //
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = mutNickname,
                    onValueChange = { mutNickname = it },
                    shape = RoundedCornerShape(28.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        backgroundColor = colorResource(id = R.color.field_color)
                    ),
                    label = {
                        Text(
                            "Enter your nickname",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 18.sp
                        )
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = mutPass,
                    onValueChange = { mutPass = it },
                    shape = RoundedCornerShape(28.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        backgroundColor = colorResource(id = R.color.field_color)
                    ),
                    label = {
                        Text(
                            "Enter your password",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 18.sp
                        )
                    },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = mutWork,
                    onValueChange = { mutWork = it },
                    shape = RoundedCornerShape(28.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        backgroundColor = colorResource(id = R.color.field_color)
                    ),
                    label = {
                        Text(
                            "What I do",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 18.sp
                        )
                    },
                )
                Spacer(modifier = Modifier.height(100.dp))
                MainButton(txt = "SIGN UP"){
                    signUpAccount(navController, mutNickname, mutPass, mutWork)
                }
            }
        }
    }
}
@Composable
fun Loader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,verticalArrangement = Arrangement.Center) {
            Text(
                text = "PLEASE WAIT...",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )

            CircularProgressIndicator(
                modifier = Modifier
                    .size(56.dp),
                strokeWidth = 4.dp,
                color = Color.Gray,
            )
        }
    }
}

@Composable
fun StartScreen() {
    val navController = LocalNavController.current
    Loader()
    LaunchedEffect(key1 = "james", block = {
        val nickname = sharedPreferences!!.getString("nickname", "")!!
        val pass = sharedPreferences!!.getString("pass", "")!!
        if (nickname != "") {
            userViewModel.checkUser(nickname, pass,
                object : UserRepositoryImpl.UserExistenceCallback {
                    override fun onUserExists(user: User) {
                        sharedPreferences!!.edit().putString("nickname", user.nickname).putString("pass", pass).apply()
                        userViewModel.curUser = user
                        chatViewModel.setListeners(user.nickname)
                        navController.navigate(Screen.Home.route)
                    }
                    override fun onUserDoesNotExist() {
                        navController.navigate(Screen.Login.route)
                    }
                }
            )
        }else{
            navController.navigate(Screen.Login.route)
        }
    })
}
@Composable
fun LogInPage(){
    var mutNickname by remember {
        mutableStateOf("")
    }
    var mutPass by remember {
        mutableStateOf("")
    }
    Surface(color = MaterialTheme.colors.background) {
        val navController = LocalNavController.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar_image_placeholder),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.6f) // Set the desired width as a fraction of the available width
                        .aspectRatio(1f) //
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = mutNickname,
                    onValueChange = { mutNickname = it },
                    shape = RoundedCornerShape(28.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        backgroundColor = colorResource(id = R.color.field_color)
                    ),
                    label = {
                        Text(
                            "Enter your nickname",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 18.sp
                        )
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = mutPass,
                    onValueChange = { mutPass = it },
                    shape = RoundedCornerShape(28.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        backgroundColor = colorResource(id = R.color.field_color)
                    ),
                    label = {
                        Text(
                            "Enter your password",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 18.sp
                        )
                    },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))
                MainButton(txt = "SIGN IN") {
                    signInAccount(navController, mutNickname, mutPass)
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegisterButton()
            }
        }
    }
}

private fun saveUserAndNavigate(navController: NavHostController, nickname: String, pass: String, work: String){
    sharedPreferences!!.edit().putString("nickname", nickname).putString("pass", pass).apply()
    userViewModel.curUser = User(nickname, pass, work)
    chatViewModel.setListeners(nickname)
    navController.navigate(Screen.Home.route)
}

fun signInAccount(navController: NavHostController, nickname: String, pass: String) {
    CoroutineScope(Dispatchers.Default).launch {
        userViewModel.checkUser(nickname, pass, object : UserRepositoryImpl.UserExistenceCallback {
            override fun onUserExists(user: User) {
                saveUserAndNavigate(navController, nickname, pass, user.work)
            }

            override fun onUserDoesNotExist() {
                Toast.makeText(navController.context, "Nickname or password is incorrect!", Toast.LENGTH_LONG).show()
            }

        })
    }

}
fun signUpAccount(navController: NavHostController, nickname: String, pass: String, work: String) {
    CoroutineScope(Dispatchers.Default).launch {
        userViewModel.addUser(nickname, pass, work, object : UserRepositoryImpl.ChildExistenceCallback {
            override fun onChildExists(dataSnapshot: DataSnapshot) {
                Toast.makeText(navController.context, "Nickname already exists!", Toast.LENGTH_LONG).show()
            }

            override fun onChildDoesNotExist() {
                saveUserAndNavigate(navController, nickname, pass, work)
            }

        })
    }

}
@Composable
fun MainButton(txt: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = R.color.button_color),
        )
    ) {
        Text(
            txt,
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
fun RegisterButton() {
    val navController = LocalNavController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Not registered?",
            fontSize = 18.sp,
            color = Color.Gray
        )
        OutlinedButton(
            onClick = { navController.navigate(Screen.SignUp.route) },
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,

                )
        ) {
            Text(
                "SIGN UP",
                fontSize = 18.sp,
                color = Color.Gray
            )
        }
    }
}