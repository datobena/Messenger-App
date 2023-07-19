package ge.dbenadshis.messengerapp

import android.app.Application
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MessengerApp() : Application() , ViewModelStoreOwner{
    override val viewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }


}