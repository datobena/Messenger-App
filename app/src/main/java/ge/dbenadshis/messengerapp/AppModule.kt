package ge.dbenadshis.messengerapp

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    @Named("users")
    fun provideFirebaseInstanceUsers(): DatabaseReference{
        return FirebaseDatabase.getInstance("https://messenger-app-dbds-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users")
    }

    @Provides
    @Singleton
    @Named("messages")
    fun provideFirebaseInstanceMessages(): DatabaseReference{
        return FirebaseDatabase.getInstance("https://messenger-app-dbds-default-rtdb.europe-west1.firebasedatabase.app/").getReference("messages")
    }

}