package ge.dbenadshis.messengerapp.database

import android.net.Uri
import androidx.compose.runtime.MutableState


interface ImageRepository {
    fun uploadImgAndSaveURL(imageUri: Uri, userId: String, isLoading: MutableState<Boolean>)
}
