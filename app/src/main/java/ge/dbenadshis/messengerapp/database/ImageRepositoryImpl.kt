package ge.dbenadshis.messengerapp.database

import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import ge.dbenadshis.messengerapp.model.User
import ge.dbenadshis.messengerapp.userViewModel
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named


class ImageRepositoryImpl @Inject constructor(
    @Named("images") private val imagesReference: StorageReference,
    @Named("users") private val usersReference: DatabaseReference
) : ImageRepository {
    override fun uploadImgAndSaveURL(imageUri: Uri, userId: String) {

        // Get current user's data from Realtime Database
        val userRef = usersReference.child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUser = dataSnapshot.getValue(User::class.java)

                // Delete the current user's existing image in Firebase Storage
                if (currentUser != null && currentUser.avatarURL.isNotEmpty()) {
                    val oldImageRef = imagesReference.storage.getReferenceFromUrl(currentUser.avatarURL)
                    oldImageRef.delete().addOnSuccessListener {
                        uploadNewImage(imageUri, imagesReference, userRef)
                    }.addOnFailureListener { exception ->
                        // Handle failure to delete old image
                        // ...
                    }
                } else {
                    uploadNewImage(imageUri, imagesReference, userRef)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle cancellation
                // ...
            }
        })
    }
    private fun uploadNewImage(
        imageUri: Uri,
        storageRef: StorageReference,
        userRef: DatabaseReference
    ) {
        val imagesRef = storageRef.child("avatar_icons/${UUID.randomUUID()}")

        // Upload image to Firebase Storage
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

                // Save image URL to Realtime Database
                userRef.child("avatarURL").setValue(downloadUri.toString())
                userViewModel.curUser.avatarURL = downloadUri.toString()

                // Handle success
                // ...
            } else {
                // Handle failure
                // ...
            }
        }
    }
}