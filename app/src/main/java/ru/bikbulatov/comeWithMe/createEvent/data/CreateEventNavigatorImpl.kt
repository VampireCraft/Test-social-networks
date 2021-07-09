package ru.bikbulatov.comeWithMe.createEvent.data

import androidx.fragment.app.FragmentManager
import ru.bikbulatov.comeWithMe.createEvent.domain.CreateEventNavigator
import ru.bikbulatov.comeWithMe.createEvent.ui.*

class CreateEventNavigatorImpl(
    private val fragmentManager: FragmentManager,
    private val rootId: Int
) :
    CreateEventNavigator {
    override fun openStartScreen() {
        fragmentManager
            .beginTransaction()
            .add(rootId, FragmentCreateEvent())
            .addToBackStack(FragmentCreateEvent::javaClass.name)
            .commit()
    }

    override fun openChooseColorScreen() {
        fragmentManager
            .beginTransaction()
            .add(rootId, FragmentChooseColor().newInstance(
                CreateEventTypeEnum.EVENT_COLOR,
                ""
            ))
            .addToBackStack(FragmentChooseColor::javaClass.name)
            .commit()
    }

    override fun openCameraScreen(absolutePath: String) {
        fragmentManager
            .beginTransaction()
            .add(rootId, FragmentChooseColor().newInstance(
                CreateEventTypeEnum.EVENT_CAMERA,
                absolutePath
            ))
            .addToBackStack(FragmentChooseColor::javaClass.name)
            .commit()
    }

    override fun openGalleryScreen(absolutePath: String) {
        fragmentManager
            .beginTransaction()
            .add(rootId, FragmentChooseColor().newInstance(CreateEventTypeEnum.EVENT_GALLERY, absolutePath))
            .addToBackStack(FragmentChooseColor::javaClass.name)
            .commit()
    }

    override fun openDescriptionScreen() {
        fragmentManager
            .beginTransaction()
            .add(rootId, FragmentDescription())
            .addToBackStack(FragmentDescription::javaClass.name)
            .commit()
    }

    override fun openSettingsScreen() {
        fragmentManager
            .beginTransaction()
            .add(rootId, FragmentCreateEventSettings())
            .addToBackStack(FragmentCreateEventSettings::javaClass.name)
            .commit()
    }

    override fun openTimeMoneyScreen() {
        fragmentManager
            .beginTransaction()
            .add(rootId, FragmentTimeMoney())
            .addToBackStack(FragmentTimeMoney::javaClass.name)
            .commit()
    }

    override fun openPositionScreen() {
        fragmentManager
            .beginTransaction()
            .add(rootId, FragmentCreateEventPosition())
            .addToBackStack(FragmentCreateEventPosition::javaClass.name)
            .commit()
    }
}