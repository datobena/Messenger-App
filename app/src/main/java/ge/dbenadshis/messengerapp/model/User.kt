package ge.dbenadshis.messengerapp.model

import org.mindrot.jbcrypt.BCrypt
import java.io.Serializable


data class User(
    var nickname: String = "",
    var passHash: String = "",
    var work: String = ""
): Serializable
class PasswordUtils {
    companion object {
        fun hashPassword(password: String): String {
            return BCrypt.hashpw(password, BCrypt.gensalt())
        }

        fun verifyPassword(password: String, storedHash: String): Boolean {
            return BCrypt.checkpw(password, storedHash)
        }
    }
}