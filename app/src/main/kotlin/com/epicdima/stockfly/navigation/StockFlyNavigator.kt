package com.epicdima.stockfly.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen

class StockFlyNavigator @JvmOverloads constructor(
    activity: FragmentActivity,
    containerId: Int,
    fragmentManager: FragmentManager = activity.supportFragmentManager,
    fragmentFactory: FragmentFactory = fragmentManager.fragmentFactory
) : AppNavigator(activity, containerId, fragmentManager, fragmentFactory) {

    override fun applyCommand(command: Command) {
        when (command) {
            is ReplaceAllIfNotExist -> replaceAllIfNotExist(command)
            is ForwardWithoutReplaceSameTop -> forwardWithoutReplaceSameTop(command)
            else -> super.applyCommand(command)
        }
    }

    private fun replaceAllIfNotExist(command: ReplaceAllIfNotExist) {
        when (val screen = command.screen) {
            is FragmentScreen -> {
                if (screen.screenKey !in localStackCopy) {
                    applyCommands(arrayOf(BackTo(null), Replace(screen)))
                }
            }
        }
    }

    private fun forwardWithoutReplaceSameTop(command: ForwardWithoutReplaceSameTop) {
        when (val screen = command.screen) {
            is FragmentScreen -> {
                if (screen.screenKey != localStackCopy.lastOrNull()) {
                    applyCommand(Forward(screen))
                }
            }
        }
    }

    override fun applyCommands(commands: Array<out Command>) {
        //copy stack before apply commands
        copyStackToLocal()

        for (command in commands) {
            try {
                applyCommand(command)
            } catch (e: RuntimeException) {
                errorOnApplyCommand(command, e)
            }
        }
    }

    private fun copyStackToLocal() {
        localStackCopy.clear()
        for (i in 0 until fragmentManager.backStackEntryCount) {
            localStackCopy.add(fragmentManager.getBackStackEntryAt(i).name!!)
        }
    }
}


data class ReplaceAllIfNotExist(val screen: Screen) : Command

data class ForwardWithoutReplaceSameTop(val screen: Screen) : Command