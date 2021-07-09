package ru.bikbulatov.comeWithMe.plans.domain

interface TimerCallback {
    fun onTimerTick(millisUntilFinished: Long)
    fun onTimerFinished(userId: Int, eventId: Int)
}
