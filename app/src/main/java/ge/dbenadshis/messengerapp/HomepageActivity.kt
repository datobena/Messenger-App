package ge.dbenadshis.messengerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ge.dbenadshis.messengerapp.ui.theme.MessengerAppTheme
import java.util.Date
import java.util.UUID
import kotlin.math.floor

class HomepageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessengerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    HomePage()
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
    fun SearchBar(
    ) {
        var text by remember {
            mutableStateOf("")
        }
        val vectorImagePainter: Painter = painterResource(R.drawable.background)
        Row(
            modifier = Modifier
                .fillMaxSize()
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
                    .height(60.dp),
                placeholder = {
                    Text(
                        "Search",
                        fontSize = 19.sp,
                        color = Color.Gray,
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.field_color),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),

                )

        }
    }

    @Composable
    fun HomePage() {
        val chatItems = generateRandomChatItems(10)
        val lazyListState = rememberLazyListState()
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    cutoutShape = CircleShape,
                    modifier = Modifier
                        .animateContentSize(animationSpec = tween(300))
                        .height(if (lazyListState.isScrolled) 0.dp else 56.dp)
                        .fillMaxWidth()

                ) {
                    BottomNavigation{
                        BottomNavigationItem(
                            selected = true,
                            onClick = { /*TODO*/ },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                        )
                        BottomNavigationItem(
                            selected = false,
                            onClick = { },
                            icon = { Icon(Icons.Default.Add, contentDescription = "Transparent") }
                        )
                        BottomNavigationItem(
                            selected = false,
                            onClick = { /*TODO*/ },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Profile") }
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* Handle plus button click */ },
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.Center,
            topBar = {

                TopAppBar(
                    backgroundColor = colorResource(id = R.color.background),

                    modifier = Modifier
                        .background(colorResource(R.color.background))
                        .animateContentSize(animationSpec = tween(300))
                        .height(height = if (lazyListState.isScrolled) (32 + 60).dp else getScreenHeight() * 0.3f)
                        .fillMaxWidth(),


                ) {
                    SearchBar()
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

    data class ChatItem(val id: String, val name: String, val message: String, val profileImageID: Int, val time: Long)

    fun generateRandomChatItems(count: Int): List<ChatItem> {
        val chatItems = mutableListOf<ChatItem>()

        for (i in 0 until count) {
            val id = UUID.randomUUID().toString()
            val name = "User ${i + 1}"
            val message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            chatItems.add(ChatItem(id, name, message, R.drawable.avatar_image_placeholder, Date().time))
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
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile picture
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
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
                    Text(text = chatItem.name)
                    TimeIndicator(sentTime = chatItem.time)
                }
                Text(text = chatItem.message)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MessengerAppTheme {
            HomePage()
        }
    }

    @Composable
    fun TimeIndicator(sentTime: Long) {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - sentTime
        val elapsedSeconds = floor(elapsedTime / 1000.0).toLong()
        val elapsedMinutes = floor(elapsedSeconds / 60.0).toLong()
        val elapsedHours = floor(elapsedMinutes / 60.0).toLong()
        val elapsedDays = floor(elapsedHours / 24.0).toLong()

        val timeAgo = when {
            elapsedDays > 0 -> "$elapsedDays days ago"
            elapsedHours > 0 -> "$elapsedHours hours ago"
            elapsedMinutes > 0 -> "$elapsedMinutes minutes ago"
            else -> "Just now"
        }

        Box (
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxSize()
                ){
            Text(
                text = timeAgo,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }

    private val LazyListState.isScrolled: Boolean
        get() = firstVisibleItemScrollOffset > 0

}