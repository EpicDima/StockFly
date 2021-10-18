package com.epicdima.stockfly.customtabs

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.*
import androidx.core.os.bundleOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.epicdima.stockfly.customtabs.CustomTabsHelper.getPackageNameToUse
import timber.log.Timber

class CustomTabsProvider(
    private var _context: Context?
) : CustomTabsServiceConnection(), DefaultLifecycleObserver {

    private val context: Context
        get() = _context!!

    private var client: CustomTabsClient? = null
    private var session: CustomTabsSession? = null
    private val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()

    override fun onResume(owner: LifecycleOwner) {
        bindService()
    }

    override fun onPause(owner: LifecycleOwner) {
        unbindService()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onDestroy()
    }

    private fun bindService() {
        Timber.v("bindService")
        getPackageNameToUse(context)
        CustomTabsClient.bindCustomTabsService(context, "com.android.chrome", this)
    }

    private fun unbindService() {
        Timber.v("unbindService")
        if (client != null) {
            context.unbindService(this)
        }
    }

    private fun onDestroy() {
        Timber.v("onDestroy")
        _context = null
    }

    fun mayLaunchUrl(url: String) {
        session?.mayLaunchUrl(Uri.parse(url), null, null)
    }

    fun mayLaunchManyUrls(urls: List<String>) {
        session?.mayLaunchUrl(null, null, urls.map {
            bundleOf(
                CustomTabsService.KEY_URL to Uri.parse(it)
            )
        })
    }

    fun launchUrl(url: String) {
        Timber.i("launch url '$url'")
        val uri = Uri.parse(url)

        if (launchInCustomTabs(context, uri)) {
            return
        }

        launchInBrowser(context, uri)
    }

    private fun launchInCustomTabs(context: Context, uri: Uri): Boolean {
        if (session != null) {
            builder.build().launchUrl(context, uri)
            return true
        }
        return false
    }

    private fun launchInBrowser(context: Context, uri: Uri) {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun onCustomTabsServiceConnected(
        name: ComponentName,
        customTabsClient: CustomTabsClient
    ) {
        Timber.v("onCustomTabsServiceConnected")
        client = customTabsClient
        client?.warmup(0L)
        session = client?.newSession(null)
        builder.setSession(session!!)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Timber.v("onCustomTabsServiceDisconnected")
        client = null
        session = null
    }
}