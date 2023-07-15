package ge.dbenadshis.messengerapp.database

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import ge.dbenadshis.messengerapp.TransactionState
import ge.dbenadshis.messengerapp.model.User
import ge.dbenadshis.messengerapp.userViewModel
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named


class ImageRepositoryImpl @Inject constructor(
    @Named("images") val imagesReference: StorageReference,
    @Named("users") private val usersReference: DatabaseReference
) : ImageRepository {
    override fun uploadImgAndSaveURL(
        imageUri: Uri,
        userId: String,
        result: MutableState<TransactionState>
    ) {
        val userRef = usersReference.child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUser = dataSnapshot.getValue(User::class.java)

                if (currentUser != null && currentUser.avatarURL.isNotEmpty()) {
                    val oldImageRef = imagesReference.storage.getReferenceFromUrl(currentUser.avatarURL)
                    oldImageRef.delete().addOnSuccessListener {
                        uploadNewImage(imageUri, imagesReference, userRef, result)
                    }.addOnFailureListener { exception ->
                        Log.d("uploadImageAndSaveURLErr", "Could not delete previous image from database! ${exception.message}")
                    }
                } else {
                    uploadNewImage(imageUri, imagesReference, userRef, result)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("uploadImageAndSaveURLErr", "Request was cancelled! ${databaseError.message}")
            }
        })
    }
    private fun uploadNewImage(
        imageUri: Uri,
        storageRef: StorageReference,
        userRef: DatabaseReference,
        result: MutableState<TransactionState>
    ) {
        val imagesRef = storageRef.child("avatar_icons/${UUID.randomUUID()}")

        val uploadTask = imagesRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                userRef.child("avatarURL").setValue(downloadUri.toString())
                userViewModel.curUser.avatarURL = downloadUri.toString()
                result.value = TransactionState.FINISHED
            } else {
                Log.d("uploadNewImageErr", "Could not upload new image!")

            }
        }
    }
}