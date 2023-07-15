package ge.dbenadshis.messengerapp.database

import android.net.Uri
import androidx.compose.runtime.MutableState
import ge.dbenadshis.messengerapp.TransactionState


interface ImageRepository {
    fun uploadImgAndSaveURL(imageUri: Uri, userId: String, result: MutableState<TransactionState>)
}
