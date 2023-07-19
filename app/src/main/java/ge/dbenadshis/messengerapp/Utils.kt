package ge.dbenadshis.messengerapp

class Utils {
    companion object {
        @JvmStatic
        fun String.formatDate(): String {
            val messageTime = this.toLongOrNull()
                ?: return "" // Assuming the string represents time in milliseconds
            val currentTime = System.currentTimeMillis()

            val elapsedTime = currentTime - messageTime
            val seconds = elapsedTime / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val weeks = days / 7
            val months = days / 30
            val years = days / 365

            return when {
                seconds < 60 -> "Just now"
                minutes < 60 -> "$minutes minutes ago"
                hours < 24 -> "$hours hours ago"
                days < 7 -> "$days days ago"
                weeks < 4 -> "$weeks weeks ago"
                months < 12 -> "$months months ago"
                else -> "$years years ago"
            }
        }
    }
}