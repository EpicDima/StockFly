package com.epicdima.stockfly.customtabs

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import androidx.browser.customtabs.CustomTabsService
import timber.log.Timber


/**
 * Helper class for Custom Tabs.
 */
object CustomTabsHelper {

    private val BROWSERS = listOf(
        "com.android.chrome",
        "com.chrome.beta",
        "com.chrome.dev",
        "com.chrome.canary",
        "com.google.android.apps.chrome",
    )

    private var packageNameToUse: String? = null

    /**
     * Goes through all apps that handle VIEW intents and have a warmup service. Picks
     * the one chosen by the user if there is one, otherwise makes a best effort to return a
     * valid package name.
     *
     * This is **not** threadsafe.
     *
     * @param context [Context] to use for accessing [PackageManager].
     * @return The package name recommended to use for connecting to custom tabs related components.
     */
    fun getPackageNameToUse(context: Context): String? {
        if (packageNameToUse != null) return packageNameToUse

        val pm = context.packageManager

        // Get default VIEW intent handler.
        val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))
        val defaultViewHandlerPackageName =
            pm.resolveActivity(activityIntent, PackageManager.MATCH_DEFAULT_ONLY)
                ?.activityInfo?.packageName

        Timber.i("defaultViewHandlerPackageName %s", defaultViewHandlerPackageName)

        // Get all apps that can handle VIEW intents.
        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs = mutableListOf<String>()

        // Get all apps that can handle service calls.
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
                .setAction(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION)
                .setPackage(info.activityInfo.packageName)
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName)
            }
        }

        Timber.i("packagesSupportingCustomTabs %s", packagesSupportingCustomTabs.joinToString(" "))

        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls.
        if (packagesSupportingCustomTabs.isEmpty()) {
            packageNameToUse = null
        } else if (packagesSupportingCustomTabs.size == 1) {
            packageNameToUse = packagesSupportingCustomTabs[0]
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
            && !hasSpecializedHandlerIntents(context, activityIntent)
            && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)
        ) {
            packageNameToUse = defaultViewHandlerPackageName
        } else {
            for (browserPackageName in BROWSERS) {
                if (packagesSupportingCustomTabs.contains(browserPackageName)) {
                    packageNameToUse = browserPackageName
                    break
                }
            }
        }

        return packageNameToUse
    }

    /**
     * Used to check whether there is a specialized handler for a given intent.
     * @param intent The intent to check with.
     * @return Whether there is a specialized handler for the given intent.
     */
    private fun hasSpecializedHandlerIntents(context: Context, intent: Intent): Boolean {
        try {
            val handlers = context.packageManager.queryIntentActivities(
                intent,
                PackageManager.GET_RESOLVED_FILTER
            )

            if (handlers.isEmpty()) {
                return false
            }

            for (resolveInfo in handlers) {
                val filter = resolveInfo.filter ?: continue

                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) {
                    continue
                }

                if (resolveInfo.activityInfo == null) {
                    continue
                }

                return true
            }
        } catch (e: RuntimeException) {
            Timber.e(e, "Runtime exception while getting specialized handlers")
        }

        return false
    }
}