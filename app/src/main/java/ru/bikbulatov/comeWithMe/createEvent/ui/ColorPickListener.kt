package ru.bikbulatov.comeWithMe.createEvent.ui

import ru.bikbulatov.comeWithMe.createEvent.domain.models.ColorGradient

interface ColorPickListener {
    fun onColorPicked(color: ColorGradient)
}