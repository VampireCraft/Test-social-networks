package ru.bikbulatov.comeWithMe.createEvent.domain

interface CreateEventNavigator {
    fun openStartScreen()
    fun openChooseColorScreen()
    fun openCameraScreen(absolutePath: String)
    fun openGalleryScreen(absolutePath: String)
    fun openDescriptionScreen()
    fun openSettingsScreen()
    fun openTimeMoneyScreen()
    fun openPositionScreen()
}