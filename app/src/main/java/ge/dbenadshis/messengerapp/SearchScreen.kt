package ge.dbenadshis.messengerapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ge.dbenadshis.messengerapp.model.User


@Composable
fun SearchScreen() {
    userViewModel.setAllUsers()
    Scaffold(
        topBar = { SearchBar() }
    ) {
        val isSearching by userViewModel.isSearching.collectAsState()
        val profiles by userViewModel.persons.collectAsState()
        if (isSearching) {
            Loader()
        } else {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(profiles) { profile ->
                    if (profile.nickname != userViewModel.curUser.nickname)
                        ProfileItem(profile)
                }
            }
        }
    }
}

@Composable
fun ProfileItem(user: User) {
    var isChat by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                isChat = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.avatar_image_placeholder), // TODO: change
            contentDescription = "Profile Photo",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = user.nickname,
                fontSize = 18.sp
            )
            Text(
                text = user.work,
                fontSize = 14.sp, color = Color.Gray
            )
        }
        if (isChat) {
            chatViewModel.getNickname(user.nickname)
            chatViewModel.currentChatFriend = user
            LocalNavController.current.navigate(Screen.Chat.route)
        }
    }
}

@Composable
fun SearchBar() {
    val searchText by userViewModel.searchText.collectAsState()
    Row(
        modifier = Modifier
            .height(100.dp)
            .background(colorResource(id = R.color.search_color))
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            Icons.Default.ArrowBack,
            contentDescription = "back",
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
                .clickable {
                    Log.d("Search page", "handle returning to home")
                },
            tint = Color.White,
        )
        TextField(
            value = searchText,
            onValueChange = userViewModel::onSearchTextChange,
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(28.dp),
                    tint = Color.Black,
                )
            },
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(horizontal = 16.dp),
            placeholder = { Text("Search", fontSize = 19.sp, color = Color.Black) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = colorResource(id = R.color.search_back),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }
}
