import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import java.io.File

class SendMessageFromTelegramBot {
    fun sendMessage(message: String, userID: String) {
        Unirest.setTimeouts(0, 0)
        val token = File("token.txt")
        val response: HttpResponse<String> =
            Unirest.post("https://api.telegram.org/${token.readText()}/sendMessage")
                .field("chat_id", userID)
                .field("text", message)
                .asString()
    }
}