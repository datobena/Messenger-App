package ge.dbenadshis.messengerapp.model

data class Message(
    var sender: String = "",
    var receiver: String = "",
    var message: String = "",
    var date: String = ""
)
