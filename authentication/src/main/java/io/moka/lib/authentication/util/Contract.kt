package io.moka.lib.authentication.util

object Contract {

    const val ACCOUNT_TYPE = "io.moka.dayday"

    var APP_TYPE: String = ""
    const val LABEL_ALARM = "Alarm"
    const val LABEL_DIARY = "Diary"
    const val LABEL_POMODORO = "Pomodoro"
    const val LABEL_BOOK = "Book"
    const val LABEL_HEALTH = "Health"
    const val LABEL_PROOF = "Proof"

    /**
     * init preference
     *
     */

    /*
    1 : 알람
    2 : 뽀모도로
    3 : 일기
    4 : 독서
    5 : 운동
    6 : 목표 설정
     */
    var auth_fromApp = 1

}