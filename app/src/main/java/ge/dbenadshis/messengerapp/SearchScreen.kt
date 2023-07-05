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


@Composable
fun SearchScreen() {
    var profiles  by remember {
        mutableStateOf(listOf<Profile>())
    }
    profiles = generateDummyProfiles()

    Scaffold(
        topBar = { SearchBar() }
    ){
        LazyColumn(modifier = Modifier.padding(it)) {
            items(profiles) { profile ->
                ProfileItem(profile = profile)
            }
        }
    }
}

@Composable
fun ProfileItem(profile: Profile) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                Log.d("Profile", "${profile.name} : ${profile.subtitle}")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = profile.photoResId),
            contentDescription = "Profile Photo",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = profile.name,
                fontSize = 18.sp
            )
            Text(
                text = profile.subtitle,
                fontSize = 14.sp, color = Color.Gray
            )
        }
    }
}
data class Profile(
    val name: String,
    val subtitle: String,
    val photoResId: Int
)

fun generateDummyProfiles(): List<Profile> {
    return listOf(
        Profile("John Doe", "Software Engineer", R.drawable.avatar_image_placeholder),
        Profile("Jane Smith", "Product Manager", R.drawable.avatar_image_placeholder),
        Profile("Alex Johnson", "Graphic Designer", R.drawable.avatar_image_placeholder),

        )
}
@Composable
fun SearchBar(){
    var text by remember {
        mutableStateOf("")
    }
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
            value = text,
            onValueChange = { text = it },
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
