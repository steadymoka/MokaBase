package io.moka.authentication.util

object Contract {

    const val ACCOUNT_TYPE = "io.moka.dayday"

    const val JOIN_DAYDAY = "JOIN DAYDAY" // 다른 앱들에서 부르는 건지

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