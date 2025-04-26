package appVersionProvider

import android.content.Context
import ygmd.kmpquiz.domain.appVersionProvider.AppVersionProvider

const val DEFAULT_APP_VERSION_NAME = "0.0.0"

class AndroidAppVersionProvider(private val context: Context): AppVersionProvider {
    override fun getAppVersionName(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: DEFAULT_APP_VERSION_NAME
        } catch (e: Exception) {
            DEFAULT_APP_VERSION_NAME
        }
    }
}