package ge.dbenadshis.messengerapp


import android.os.Bundle

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

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ge.dbenadshis.messengerapp.ui.theme.MessengerAppTheme

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
}
@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen()
        }
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessengerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
//                    SetupNavGraph()
                    SearchScreen()
//                    ChatScreen()
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(){
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
                TextFieldFun(txt = "Enter your nickname")
                Spacer(modifier = Modifier.height(16.dp))
                TextFieldFun(txt = "Enter your password")
                Spacer(modifier = Modifier.height(16.dp))
                TextFieldFun(txt = "What I do")
                Spacer(modifier = Modifier.height(100.dp))
                MainButton(txt = "SIGN UP")
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    Surface(color = MaterialTheme.colors.background) {
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
                    contentDescription = "Logo" ,
                    modifier = Modifier
                        .fillMaxWidth(0.6f) // Set the desired width as a fraction of the available width
                        .aspectRatio(1f) //
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextFieldFun(txt = "Enter your nickname")
                Spacer(modifier = Modifier.height(16.dp))
                TextFieldFun(txt = "Enter your password")
                Spacer(modifier = Modifier.height(16.dp))
                MainButton(txt = "SIGN IN")
                Spacer(modifier = Modifier.height(100.dp))
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegisterButton(navController)
            }
        }
    }
}

@Composable
fun TextFieldFun(txt: String){
    if (txt.endsWith("password")){
        PasswordTextField(txt)
        return
    }
    var text by remember {
        mutableStateOf("")
    }
    TextField(
        value = text,
        onValueChange = {text = it},
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            backgroundColor = colorResource(id = R.color.field_color)
        ),
        label = {
            Text(
                txt,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 18.sp)
        },
    )
}
@Composable
fun PasswordTextField(txt: String){
    var text by remember {
        mutableStateOf("")
    }
    TextField(
        value = text,
        onValueChange = {text = it},
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            backgroundColor = colorResource(id = R.color.field_color)
        ),
        label = {
            Text(
                txt,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 18.sp)
        },
        visualTransformation = PasswordVisualTransformation()
    )
}
@Composable
fun MainButton(txt: String){
    Button(
        onClick = { },
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
fun RegisterButton(navController: NavHostController){
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
            onClick = {navController.navigate(Screen.SignUp.route)},
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