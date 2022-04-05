import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class checkTime {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    fun check(webLink: String) {
        val request = Request.Builder()
            .url(webLink + "api/signals/ao_signals/?symbol=1&ordering=order")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Token 54f94af4b3f803e89f31d218f59d8c04beb3c97d")
            .build()
        client.newCall(request).execute().use { response ->
            println(response)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentTimestamp = System.currentTimeMillis() // Время в секундах
            val listAll = JSONObject(response.body()?.string()).get("results") as JSONArray// Общий ответ от стенда c ТФ
            val users = File("users_id.txt") // Список ID пользователей с телеги
            val logStat = File(LocalDate.now().toString() + " logStat.txt") // Файл с логами
            for (i in listAll) { // Проверка, что смотрим часовой ТФ
                if (JSONObject(JSONObject(i.toString()).get("timeframe").toString()).get("timeframe").equals("1h")) {
                    val timeForResponse =
                        (JSONObject(i.toString()).get("update_stamp")).toString().replace(".", "").substring(0, 13)
                            .toLong()
                    if (currentTimestamp - timeForResponse < 1200000
                    ) {
                        println(
                            "Время в запросе  отличается МЕНЕЕ чем на 20 минут\n Время на стенде ${
                                sdf.format(timeForResponse)
                            }\n Время текущее   ${sdf.format(currentTimestamp)}"
                        )
                        logStat.appendText(
                            (LocalDateTime.now()
                                .toString() + " Время в запросе  отличается МЕНЕЕ чем на ${(currentTimestamp - timeForResponse) / 1000 / 60} мин\\n\" +\n" +
                                    "                                        \" Время на стенде ${
                                        sdf.format(timeForResponse)
                                    }\\n\" +\n" +
                                    "                                        \" Время текущее   ${
                                        sdf.format(
                                            currentTimestamp
                                        )
                                    }\n")
                        )
                    } else {
                        println(
                            "Время в запросе  отличается БОЛЕЕ чем на ${(currentTimestamp - timeForResponse) / 1000 / 60} мин\n Время на стенде ${
                                sdf.format(timeForResponse)
                            }\n Время текущее   ${sdf.format(currentTimestamp)}"
                        )
                        for (usersId in users.readText().split(",")) { // Отправка сообщений в ТГ бота
                            SendMessageFromTelegramBot().sendMessage(
                                "Время в запросе  отличается БОЛЕЕ чем на ${(currentTimestamp - timeForResponse) / 1000 / 60} мин\n" +
                                        " Время на стенде ${
                                            sdf.format(timeForResponse)
                                        }\n" +
                                        " Время текущее   ${sdf.format(currentTimestamp)}", usersId
                            )
                        }
                        logStat.appendText(
                            (LocalDateTime.now()
                                .toString() + " Время в запросе  отличается БОЛЕЕ чем на ${(currentTimestamp - timeForResponse) / 1000 / 60} мин\\n\" +\n" +
                                    "                                        \" Время на стенде ${
                                        sdf.format(timeForResponse)
                                    }\\n\" +\n" +
                                    "                                        \" Время текущее   ${
                                        sdf.format(
                                            currentTimestamp
                                        )
                                    }\n")
                        )

                        Assert.fail(
                            "Время в запросе  отличается БОЛЕЕ чем на ${(currentTimestamp - timeForResponse) / 1000 / 60} мин\n Время на стенде ${
                                sdf.format(timeForResponse)
                            }\n Время текущее   ${sdf.format(currentTimestamp)}"
                        )
                    }
                    break
                } else {
                    println("Часовой ТФ в запросе со стенда не обнаружен")
                    Assert.fail("Часовой ТФ в запросе со стенда не обнаружен")
                }
            }
        }
    }
}