package ge.dbenadshis.messengerapp

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.floor




@Composable
fun ProfilePage(navController: NavController) {
    Scaffold(
        bottomBar = {
            AddBottomAppBar(navController, null)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle plus button click */ },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 0.dp
                ),
                backgroundColor = colorResource(id = R.color.background)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.avatar_image_placeholder),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(horizontal = getScreenWidth() * 0.2f)
                    .fillMaxSize(0.6f)
                    .clickable { /* Handle image click */ }
                    .clip(CircleShape)
            )

            TextField(
                value = "John Doe",
                onValueChange = { },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.field_color),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 20.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .background(Color.White)
                    .clip(CircleShape),
            )

            // Profession TextField
            TextField(
                value = "Software Engineer", // Replace with your state or variable
                onValueChange = { /* Handle profession change */ },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.field_color),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 20.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .clip(CircleShape),
            )

            // Update Button
            Button(
                onClick = { /* Handle update button click */ },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.35f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.background),
                    disabledBackgroundColor = Color.LightGray,
                    disabledContentColor = Color.Gray,
                ),
            ) {
                Text("Update", color = Color.White, fontSize = 20.sp)
            }

            // Sign Out Button
            OutlinedButton(
                onClick = { /* Handle sign out button click */ },
                modifier = Modifier.padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.DarkGray,
                    disabledBackgroundColor = Color.LightGray,
                    disabledContentColor = Color.Gray,
                ),
                border = BorderStroke(1.5.dp, Color.Gray),
                elevation = ButtonDefaults.elevation(0.dp)
            ) {
                Text(
                    "Sign Out",
                    fontSize = 20.sp,
                )
            }
        }

    }

}

@Composable
fun getScreenHeight(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.dp
}

@Composable
fun getScreenWidth(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}

@Composable
fun TopSearchBar() {
    var text by remember {
        mutableStateOf("")
    }
    val vectorImagePainter: Painter = painterResource(R.drawable.background)
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
            .paint(
                vectorImagePainter,
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Crop,
            ),
        verticalAlignment = Alignment.Bottom,


        ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                    tint = Color.DarkGray,
                )
            },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(60.dp)
                .alpha(0.75f),
            placeholder = {
                Text(
                    "Search",
                    fontSize = 19.sp,
                    color = Color.DarkGray,
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = colorResource(id = R.color.field_color),
                textColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),

            )

    }
}

@Composable
fun HomePage(navController: NavController) {
    val chatItems = generateRandomChatItems(6)
    val lazyListState = rememberLazyListState()
    Scaffold(
        bottomBar = {
            AddBottomAppBar(navController, lazyListState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle plus button click */ },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 0.dp
                ),
                backgroundColor = colorResource(id = R.color.background)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {

            Box(

                modifier = Modifier
                    .background(colorResource(R.color.background))
                    .animateContentSize(animationSpec = tween(300))
                    .height(maxOf(92.dp, getScreenHeight() * 0.3f - lazyListState.scrolled))
                    .fillMaxWidth(),


                ) {
                TopSearchBar()
            }

        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            state = lazyListState
        ) {
            items(chatItems) { chatItem ->
                ChatItem(chatItem)
            }
        }
    }

}

@Composable
fun AddBottomAppBar(
    navController: NavController,
    lazyListState: LazyListState?
) {
    val isHomePage = lazyListState != null
    val visibleHeight = if (isHomePage) 56.dp - lazyListState!!.scrolled * 0.2f else 56.dp
    BottomAppBar(
        elevation = AppBarDefaults.BottomAppBarElevation,
        cutoutShape = CircleShape,
        modifier = Modifier
            .height(visibleHeight)
            .fillMaxWidth(),
        backgroundColor = Color.White,
        contentColor = Color.DarkGray,
    ) {
        BottomNavigationItem(
            selected = isHomePage,
            selectedContentColor = colorResource(id = R.color.background),
            unselectedContentColor = Color.DarkGray,
            onClick = {
                if (!isHomePage) {
                    navController.navigateUp()
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
        )
        BottomNavigationItem(
            selected = false,
            enabled = false,
            onClick = { },
            icon = { Icon(Icons.Default.Add, contentDescription = "Transparent") }
        )
        BottomNavigationItem(
            selected = !isHomePage,
            selectedContentColor = colorResource(id = R.color.background),
            unselectedContentColor = Color.DarkGray,
            onClick = {
                if (isHomePage) {
                    navController.navigate(Screen.Profile.route)
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Profile") }
        )
    }

}


data class ChatItem(
    val id: String,
    val name: String,
    val message: String,
    val profileImageID: Int,
    val time: Long
)

fun generateRandomChatItems(count: Int): List<ChatItem> {
    val chatItems = mutableListOf<ChatItem>()

    for (i in 0 until count) {
        val id = UUID.randomUUID().toString()
        val name = "User ${i + 1}"
        val message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        chatItems.add(
            ChatItem(
                id,
                name,
                message,
                R.drawable.avatar_image_placeholder,
                Date().time
            )
        )
    }

    return chatItems
}

@Composable
fun ChatItem(chatItem: ChatItem) {
    // Display the chat item with its details
    // Customize this composable to match your chat item UI
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Profile picture
        Surface(
            shape = CircleShape,
            modifier = Modifier.size(48.dp),
        ) {
            Image(
                painter = painterResource(id = chatItem.profileImageID),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Name and message
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Row {
                Text(
                    text = chatItem.name,
                    modifier = Modifier.padding(8.dp)
                )
                TimeIndicator(sentTime = chatItem.time)
            }
            Text(
                text = chatItem.message,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun TimeIndicator(sentTime: Long) {
    val currentTime = System.currentTimeMillis()
    val elapsedTime = currentTime - sentTime
    val elapsedSeconds = floor(elapsedTime / 1000.0).toLong()
    val elapsedMinutes = floor(elapsedSeconds / 60.0).toLong()
    val elapsedHours = floor(elapsedMinutes / 60.0).toLong()
    val locale = Locale.getDefault()
    val elapsedDays = floor(elapsedHours / 24.0).toLong()

    val timeAgo = when {
        elapsedDays > 0 -> SimpleDateFormat("d MMM", locale).format(currentTime).uppercase()
        elapsedHours > 0 -> "$elapsedHours hour"
        elapsedMinutes > 0 -> "$elapsedMinutes min"
        else -> "Just now"
    }

    Box(
        modifier = Modifier
            .padding(start = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = timeAgo,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}

private val LazyListState.scrolled: Dp
    get() = (firstVisibleItemScrollOffset + (firstVisibleItemIndex * 512)).dp

