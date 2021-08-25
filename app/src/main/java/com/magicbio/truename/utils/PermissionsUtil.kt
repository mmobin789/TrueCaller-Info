package com.magicbio.truename.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.magicbio.truename.R
import com.magicbio.truename.TrueName


object PermissionsUtil {

    private const val readCallLogContactsRC = 1
    private const val locationAndSendSmsRC = 2
    private const val smsRC = 3
    private const val callAndReadPhoneNumbersRC = 4
    private const val readPhoneStateAndSystemWindowRC = 5
    private const val locationRC = 6

    private const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private const val sendSMSPermission = Manifest.permission.SEND_SMS
    private const val readCallLogPermission = Manifest.permission.READ_CALL_LOG
    private const val readContactsPermission = Manifest.permission.READ_CONTACTS

    //  private const val systemWindowPermission = Manifest.permission.SYSTEM_ALERT_WINDOW
    private const val readPhoneStatePermission = Manifest.permission.READ_PHONE_STATE
    private const val makeCallsPermissions = Manifest.permission.CALL_PHONE


    @RequiresApi(Build.VERSION_CODES.O)
    private const val readPhoneNumbersPermission = Manifest.permission.READ_PHONE_NUMBERS
    private val onPermissionsMap = HashMap<Int, Pair<() -> Unit, () -> Unit>>(6)


    @JvmStatic
    fun checkPhoneStatePermission(
        appCompatActivity: AppCompatActivity?,
        onPermission: () -> Unit
    ) {

        checkPermissions(
            readPhoneStateAndSystemWindowRC,
            arrayOf(readPhoneStatePermission),
            R.string.grant_window_permission,
            onPermission,
            appCompatActivity
        )

    }

    @JvmStatic
    fun checkCallAndReadPhoneNumbersPermission(
        appCompatActivity: AppCompatActivity?,
        onPermission: () -> Unit
    ) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            arrayOf(makeCallsPermissions, readPhoneNumbersPermission)
        else arrayOf(makeCallsPermissions)

        checkPermissions(
            callAndReadPhoneNumbersRC,
            permissions,
            R.string.grant_call_permission,
            onPermission,
            appCompatActivity
        )

    }

    @JvmStatic
    fun checkReadCallLogContactsAndSendSMSPermissions(
        appCompatActivity: AppCompatActivity?,
        onPermission: () -> Unit
    ) {

        checkPermissions(
            readCallLogContactsRC,
            arrayOf(readCallLogPermission, readContactsPermission, sendSMSPermission),
            R.string.grant_numbers_permission,
            onPermission,
            appCompatActivity
        )

    }

    fun checkLocationAndSendSMSPermission(
        appCompatActivity: AppCompatActivity?,
        onPermission: () -> Unit
    ) {

        checkPermissions(
            locationAndSendSmsRC,
            arrayOf(locationPermission, sendSMSPermission),
            R.string.grant_location_permission,
            onPermission,
            appCompatActivity
        )

    }

    fun checkLocationPermission(
        appCompatActivity: AppCompatActivity?,
        onPermission: () -> Unit
    ) {
        checkPermissions(
            locationRC,
            arrayOf(locationPermission),
            R.string.grant_location_only_permission,
            onPermission,
            appCompatActivity
        )
    }

    fun checkSendSmsPermission(
        appCompatActivity: AppCompatActivity?,
        onPermission: () -> Unit
    ) {

        checkPermissions(
            smsRC,
            arrayOf(sendSMSPermission),
            R.string.grant_sms_permission,
            onPermission,
            appCompatActivity
        )

    }


    /**
     * Method checks for permissions and requests for it if not granted.
     */
    private fun checkPermissions(
        requestCode: Int,
        permissions: Array<String>,
        @StringRes permissionsMessage: Int,
        onPermission: () -> Unit,
        appCompatActivity: AppCompatActivity?
    ) {
        onPermissionsMap[requestCode] = Pair(onPermission, { onError(permissionsMessage) })

        appCompatActivity?.run {
            val notGranted = permissions.filter {
                notHasPermission(this, it)
            }

            if (notGranted.isNotEmpty())
                requestPermissions(notGranted.toTypedArray(), requestCode)
            else onPermissionsMap[requestCode]?.first?.invoke()


        } ?: run {
            onPermissionsMap[requestCode]?.second?.invoke()
        }
    }

    private fun onError(@StringRes error: Int) =
        Toast.makeText(
            TrueName.getInstance(),
            error,
            Toast.LENGTH_LONG
        ).show()


    @JvmStatic
    fun onPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        onPermissionsDenied: ((Int) -> Unit)?
    ) {
        if (grantResults.isNotEmpty() && permissions.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onPermissionsMap[requestCode]?.first?.invoke()
        } else {
            onPermissionsMap[requestCode]?.second?.invoke()
            onPermissionsDenied?.invoke(requestCode)
        }


    }


    private fun notHasPermission(context: Context, permission: String): Boolean {
        return (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED)
    }
}