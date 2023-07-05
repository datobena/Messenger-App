package ge.dbenadshis.messengerapp

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    fun provideFirebaseInstance(): DatabaseReference{
        return FirebaseDatabase.getInstance("https://messenger-app-dbds-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")
    }


}