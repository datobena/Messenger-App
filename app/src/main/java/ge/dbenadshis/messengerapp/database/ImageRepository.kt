package ge.dbenadshis.messengerapp.database

import android.net.Uri


interface ImageRepository {
    fun uploadImgAndSaveURL(imageUri: Uri, userId: String)
}
