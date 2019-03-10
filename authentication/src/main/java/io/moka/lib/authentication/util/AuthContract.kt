package io.moka.lib.authentication.util

enum class App(var id: String, var from: Int = 0) {
    Base("Base"),
    Alarm("Alarm", 1), // 출시
    Pomodoro("Pomodoro", 2), // 출시
    Diary("Diary", 3), // 출시
    Book("Book", 4),
    Health("Health", 5),
    Social("Social"),
    Together("Together"),
    Talk("Talk");
}

object AuthContract {

    private const val ACCOUNT_TYPE_BASE = "io.moka.dayday"

    var currentApp: App = App.Base
        private set(value) {
            field = value
        }

    var ACCOUNT_TYPE = ACCOUNT_TYPE_BASE
        private set(value) {
            field = value
        }

    /* Initialize */

    fun setApp(app: App) {
        currentApp = app
    }

}
