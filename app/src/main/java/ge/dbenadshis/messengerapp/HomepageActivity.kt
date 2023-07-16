package ge.dbenadshis.messengerapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import ge.dbenadshis.messengerapp.model.Message
import ge.dbenadshis.messengerapp.model.User

import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.floor


@Composable
fun DrawAvatar(imageUri: MutableState<Uri?>, bitmap: MutableState<Bitmap?>) {

    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            imageUri.value = uri
        }

    if (bitmap.value == null || imageUri.value.toString() != userViewModel.curUser.avatarURL) {
        if (imageUri.value != null) {
            if (imageUri.value.toString().startsWith("http")) {
                LaunchedEffect(imageUri.value) {
                    downloadBitmap(bitmap, imageUri.value, isLoading)
                }
            } else if (Build.VERSION.SDK_INT < 28) {
                bitmap.value =
                    MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri.value)
            } else {
                val source =
                    ImageDecoder.createSource(context.contentResolver, imageUri.value!!)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }
        } else {
            bitmap.value = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.avatar_image_placeholder
            )
        }
    }

    if (isLoading.value) {
        Loader()
    } else {
        if (bitmap.value != null) {
            val bitmapImage = bitmap.value!!.asImageBitmap()
            Image(
                bitmap = bitmapImage,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(horizontal = getScreenWidth() * 0.2f, vertical = 32.dp)
                    .aspectRatio(1f)
                    .fillMaxSize(0.6f)
                    .clickable { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                    .clip(CircleShape)
            )
        } else {
            // Placeholder or error image when bitmap is null
            Image(
                painter = painterResource(R.drawable.avatar_image_placeholder),
                contentDescription = "Profile Image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(horizontal = getScreenWidth() * 0.2f, vertical = 32.dp)
                    .aspectRatio(1f)
                    .fillMaxSize(0.6f)
                    .clickable { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                    .clip(CircleShape)
            )
        }
    }
}

suspend fun downloadBitmap(
    bitmap: MutableState<Bitmap?>,
    imageUri: Uri?,
    loading: MutableState<Boolean>?
) {
    loading?.value = true
    val storageRef =
        userViewModel.imageRepo.imagesReference.storage.getReferenceFromUrl(imageUri.toString())
    val byteArray = storageRef.getBytes(10 * 1024 * 1024).await()
    bitmap.value = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    loading?.value = false
}


@Composable
fun ProfilePage() {
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current
    val curState = remember { mutableStateOf(TransactionState.FINISHED) }
    val avatarUriMut = remember {
        mutableStateOf(userViewModel.curUser.avatarURL.let { if (it == "") null else it.toUri() })
    }
    val nickname = remember {
        mutableStateOf(userViewModel.curUser.nickname)
    }
    val work = remember {
        mutableStateOf(userViewModel.curUser.work)
    }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val mutMissingField = remember {
        mutableStateOf(MissingFields.NONE)
    }
    if (curState.value == TransactionState.LOADING)
        Loader()
    else {
        avatarUriMut.value =
            userViewModel.curUser.avatarURL.let { if (it == "") null else it.toUri() }
        Scaffold(
            bottomBar = {
                AddBottomAppBar(navController, null)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.Search.route) },
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
                DrawAvatar(avatarUriMut, bitmap)
                HandleMissingFieldMessage(mutMissingField.value)
                if (curState.value == TransactionState.FINISHED_EXISTS) {
                    Text(text = "* Nickname is already taken!", color = Color.Red)
                }
                TextField(
                    value = nickname.value,
                    onValueChange = { newNickname -> nickname.value = newNickname },
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

                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )

                // Profession TextField
                TextField(
                    value = work.value,
                    onValueChange = { newWork -> work.value = newWork },
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

                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        userViewModel.updateCurUser(
                            nickname.value,
                            work.value,
                            avatarUriMut.value,
                            curState,
                            mutMissingField
                        )
                        focusManager.clearFocus()
                    })
                )

                // Update Button
                Button(
                    onClick = {
                        userViewModel.updateCurUser(
                            nickname.value,
                            work.value,
                            avatarUriMut.value,
                            curState,
                            mutMissingField
                        )
                    },
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
                    onClick = {
                        sharedPreferences!!.edit().clear().apply()
                        chatViewModel.reset()
                        chatViewModel.clearNickname()
                        navController.navigate(Screen.Start.route)
                    },
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
fun TopSearchBar(
    chatItems: MutableState<List<ChatItem>>,
    filteredChatItems: MutableState<List<ChatItem>>
) {
    var text by remember {
        mutableStateOf("")
    }
    val vectorImagePainter: Painter = painterResource(R.drawable.background)
    val focusManager = LocalFocusManager.current
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
            onValueChange = {
                text = it
                filteredChatItems.value = chatItems.value.filter {item -> item.user.nickname.contains(text) }
            },
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

            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })

        )

    }
}


suspend fun generateItems(
    messages: List<Message>,
    curState: MutableState<TransactionState>
): List<ChatItem> {
    curState.value = TransactionState.LOADING
    val m: HashMap<String, ChatItem> = HashMap()
    for (elem in messages) {
        val temp = if (elem.isSentByCurrentUser) elem.receiver else elem.sender
        val user = userViewModel.getNicknameFrom(temp)
        if (user.nickname == userViewModel.curUser.nickname) continue
        if (m.containsKey(temp)) {
            if (m[temp]!!.time < elem.date.toLong()) {
                m[temp] = ChatItem(user.nickname, elem.message, elem.date.toLong(), user)
            }
        } else {
            m[temp] = ChatItem(user.nickname, elem.message, elem.date.toLong(), user)
        }
    }
    val res = m.values.toMutableList()
    res.sortBy { it.time }
    res.reverse()
    curState.value = TransactionState.FINISHED
    return res
}

@Composable
fun HomePage() {
    val navController = LocalNavController.current
    val messages by chatViewModel.allMessages.observeAsState(listOf())
    val curState = remember { mutableStateOf(TransactionState.FINISHED) }
    val chatItems = remember {
        mutableStateOf(listOf<ChatItem>())
    }
    val filteredChatItems = remember {
        mutableStateOf(listOf<ChatItem>())
    }
    LaunchedEffect(messages) {
        println(messages.toString())
        chatItems.value = generateItems(messages, curState)
        filteredChatItems.value = chatItems.value
    }
    val lazyListState = rememberLazyListState()
    if (curState.value == TransactionState.LOADING)
        Loader()
    else {
        Scaffold(
            bottomBar = {
                AddBottomAppBar(navController, lazyListState)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.Search.route) },
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
                    TopSearchBar(chatItems, filteredChatItems)
                }

            }
        ) {
            LazyColumn(
                modifier = Modifier.padding(it),
                state = lazyListState,

            ) {
                items(filteredChatItems.value) { chatItem ->
                    ChatItem(chatItem)
                }
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
            onClick = { navController.navigate(Screen.Search.route) },
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
    val key: String,
    val message: String,
    val time: Long,
    val user: User = User(),
    val bitmapImage: MutableState<Bitmap?> = mutableStateOf(null)
)


@Composable
fun ChatItem(chatItem: ChatItem) {
    val navController = LocalNavController.current
    val uri = chatItem.user.avatarURL.let { if (it == "") null else it.toUri() }
    val context = LocalNavController.current.context
    if (uri != null)
        LaunchedEffect(chatItem.user.nickname) {
            downloadBitmap(chatItem.bitmapImage, uri, null)
        }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                chatViewModel.getNickname(chatItem.key)
                chatViewModel.currentChatFriend = chatItem.user
                navController.navigate(Screen.Chat.route)
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Profile picture
        Surface(
            shape = CircleShape,
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp)
                .size(48.dp)
        ) {
            Image(
                bitmap = if (chatItem.bitmapImage.value == null) BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.avatar_image_placeholder
                ).asImageBitmap() else chatItem.bitmapImage.value!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Name and message

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = chatItem.key,
                modifier = Modifier.padding(8.dp),
                fontSize = 22.sp,
                color = Color.DarkGray
            )
            Text(
                text = chatItem.message,
                modifier = Modifier.padding(8.dp),
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.Top)
        ) {
            TimeIndicator(sentTime = chatItem.time)
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


    Text(
        text = timeAgo,
        color = Color.Gray,
        modifier = Modifier
            .padding(8.dp)
    )
}

private val LazyListState.scrolled: Dp
    get() = (firstVisibleItemScrollOffset + (firstVisibleItemIndex * 512)).dp

