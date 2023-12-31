import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import ge.dbenadshis.messengerapp.Loader
import ge.dbenadshis.messengerapp.LocalNavController
import ge.dbenadshis.messengerapp.R
import ge.dbenadshis.messengerapp.Screen
import ge.dbenadshis.messengerapp.Utils.Companion.formatDate
import ge.dbenadshis.messengerapp.chatViewModel
import ge.dbenadshis.messengerapp.downloadBitmap
import ge.dbenadshis.messengerapp.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatScreen(currentUser: String, friend: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        ChatScreenCreate(currentUser, friend)
    }
}

@Composable
fun ChatScreenCreate(currentUser: String, friend: String) {
    val state = rememberCollapsingToolbarScaffoldState()
    val enabled by remember { mutableStateOf(true) }
    val messages by chatViewModel.allMessages.observeAsState(listOf())
    val currentFriend by chatViewModel.currentFriend.collectAsState()
    val navController = LocalNavController.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val chatItem = chatViewModel.currentChatFriend
    val uri = chatItem.avatarURL.let{if(it == "") null else it.toUri()}
    val context = LocalNavController.current.context
    if(uri != null)
        LaunchedEffect(chatItem) {
            downloadBitmap(bitmap, uri, null)
        }
    if (currentFriend == "") {
        Loader()
    } else {
        Box {
            CollapsingToolbarScaffold(
                modifier = Modifier.fillMaxSize(),
                state = state,
                scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
                toolbarModifier = Modifier.background(MaterialTheme.colors.primary),
                enabled = enabled,
                toolbar = {
                    val textSize = (20 + (30 - 18) * state.toolbarState.progress).sp
                    val mn = (60 * state.toolbarState.progress).dp
                    Box(
                        modifier = Modifier
                            .background(colorResource(id = R.color.button_color))
                            .fillMaxWidth()
                            .height(150.dp)
                            .pin()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "back",
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    top = 24.dp - (12 * (1 - state.toolbarState.progress)).dp
                                )
                                .width(45.dp)
                                .height(45.dp)
                                .align(Alignment.TopStart)
                                .clickable {
                                    navController.navigate(Screen.Home.route)
                                },
                            tint = Color.White,
                        )
                        Column(
                            modifier = Modifier
                                .padding(
                                    start = 16.dp + (48 * (1 - state.toolbarState.progress)).dp,
                                    top = mn,
                                    bottom = 8.dp
                                )
                                .align(Alignment.CenterStart)
                        ) {
                            Text(
                                text = friend,
                                color = Color.White,
                                fontSize = textSize
                            )
                            Text(
                                text = chatViewModel.currentChatFriend.work,
                                color = Color.White,
                                fontSize = textSize * (0.7)
                            )
                        }
                        Image(
                            bitmap = if(bitmap.value == null) BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.avatar_image_placeholder
                            ).asImageBitmap() else bitmap.value!!.asImageBitmap(),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .padding(end = 16.dp, top = 8.dp + mn, bottom = 8.dp)
                                .size(60.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .border(2.dp, Color.Cyan, CircleShape)
                        )
                    }
                }
            ) {
                if(messages.isEmpty())
                    Box(Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                        contentAlignment = Alignment.Center) {
                        Text(
                            text = "No messages here yet...",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = MESSAGE_TEXT_FIELD)
                ) {
                    items(
                        messages
                    ) { message ->
                        if ((chatViewModel.getCurrentFriend() == message.receiver && message.isSentByCurrentUser) ||
                            (chatViewModel.getCurrentFriend() == message.sender && !message.isSentByCurrentUser)
                        ) {
                            ChatMessageItem(message)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                ) {
                    ChatBottom(onclick = { txt ->
                        CoroutineScope(Dispatchers.Default).launch {
                            chatViewModel.addMessage(
                                Message(
                                    currentUser,
                                    friend,
                                    txt.content,
                                    txt.date
                                )
                            )
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun ChatBottom(onclick: (txt: ChatMessage) -> Unit) {
    var text by remember {
        mutableStateOf("")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MESSAGE_TEXT_FIELD)
            .background(color = Color.White)
    ) {
        TextField(
            value = text,
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onclick(ChatMessage(text, System.currentTimeMillis().toString(), true))
                            text = ""
                        }
                    },
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
            },
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
            shape = RoundedCornerShape(35.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = colorResource(id = R.color.search_back)
            ),
            placeholder = {
                Text(
                    text = "Message",
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp) // Adjust the vertical padding as needed
                )
            },
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start, fontSize = 18.sp),

            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                if (text.isNotEmpty()) {
                    onclick(ChatMessage(text, System.currentTimeMillis().toString(), true))
                    text = ""
                }
            })
        )
    }
}

@Composable
fun ChatMessageItem(message: Message) {
    val isSentByCurrentUser = message.isSentByCurrentUser
    val shape = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = if (!isSentByCurrentUser) 0.dp else 24.dp,
        bottomEnd = if (isSentByCurrentUser) 0.dp else 24.dp,
    )
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (isSentByCurrentUser) {
            Text(
                text = message.date.formatDate(),
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.caption
            )
        }
        Card(
            modifier = Modifier,
            elevation = 4.dp,
            shape = shape,
            backgroundColor = getBubbleColor(isSentByCurrentUser)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = message.message, color = if (isSentByCurrentUser) colorResource(
                        id = R.color.white
                    ) else colorResource(id = R.color.dark_blue)
                )
            }
        }

        if (!isSentByCurrentUser) {
            Text(
                text = message.date.formatDate(),
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.caption
            )
        }
    }
}


@Composable
fun getBubbleColor(isSentByCurrentUser: Boolean): Color {
    return if (isSentByCurrentUser) {
        colorResource(id = R.color.search_color) // Customize the color for messages sent by the current user
    } else {
        colorResource(id = R.color.search_back) // Customize the color for other users' messages
    }
}

data class ChatMessage(
    val content: String,
    val date: String,
    val isSentByCurrentUser: Boolean = false
)

val MESSAGE_TEXT_FIELD = 90.dp
