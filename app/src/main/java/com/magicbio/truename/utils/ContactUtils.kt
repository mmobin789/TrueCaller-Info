package com.magicbio.truename.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.SmsManager
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.magicbio.truename.R
import com.magicbio.truename.TrueName
import com.magicbio.truename.activities.CallDetails
import com.magicbio.truename.db.contacts.Contact
import com.magicbio.truename.fragments.background.AppAsyncWorker
import com.magicbio.truename.models.CallLogModel
import com.magicbio.truename.retrofit.ApiClient
import com.magicbio.truename.retrofit.ApiInterface
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object ContactUtils {
    private val context = TrueName.getInstance()

    @JvmStatic
    fun openSystemBlockingApp() {
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val intent = telecomManager.createManageBlockedNumbersIntent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    //  private val fbCallbackManager = CallbackManager.Factory.create()
    /* @JvmStatic
     fun isNumberBlocked(number: String, onBlocked: (Boolean) -> Unit) {
         GlobalScope.launch {
             val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
             val intent = telecomManager.createManageBlockedNumbersIntent()
             intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
             context.startActivity(intent)
             var blocked = false
             context.contentResolver.query(
                 BlockedNumbers.CONTENT_URI, arrayOf(
                     BlockedNumbers.COLUMN_ID,
                     BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                     BlockedNumbers.COLUMN_E164_NUMBER
                 ), null, null, null
             )?.run {
                 if (count > 0) {
                     while (moveToNext()) {
                         val blockedNumber =
                             getString(getColumnIndex(BlockedNumbers.COLUMN_ORIGINAL_NUMBER))
                         if (number == blockedNumber) {
                             blocked = true
                         }
                     }

                 }
                 close()
                 withContext(Dispatchers.Main.immediate) {
                     onBlocked(blocked)
                 }
             }

         }

     }*/
    /* @JvmStatic
     fun isNumberBlocked(number: String) = BlockedNumberContract.isBlocked(context, number)


     @JvmStatic
     fun blockNumber(number: String) {
         val values = ContentValues()
         values.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
         val uri: Uri? = context.contentResolver.insert(BlockedNumbers.CONTENT_URI, values)
         val blocked = uri != null
         if (blocked)
             Toast.makeText(context, context.getString(R.string.blocked, number), Toast.LENGTH_SHORT)
                 .show()
        // return blocked
     }*/

    /* @JvmStatic
     fun unBlockNumber(number: String) {
         val values = ContentValues()
         values.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
         val contentResolver = context.contentResolver
         contentResolver.insert(BlockedNumbers.CONTENT_URI, values)?.let {
             if (contentResolver.delete(it, null, null) > 0)
                 Toast.makeText(
                     context,
                     context.getString(R.string.unblocked, number),
                     Toast.LENGTH_SHORT
                 ).show()
         }
     }*/

    @JvmStatic
    fun openCallDetailsActivity(contact: Contact) {
        val intent =
            Intent(context, CallDetails::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("name", contact.name)
        intent.putStringArrayListExtra("numbers", contact.numbers)
        intent.putExtra("email", contact.email)
        context.startActivity(intent)
    }

    @JvmStatic
    fun openCallDetailsActivity(callLogModel: CallLogModel) {
        val intent =
            Intent(context, CallDetails::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("name", callLogModel.name)
        intent.putStringArrayListExtra("numbers", arrayListOf(callLogModel.phNumber))
        intent.putExtra("by-true-name", callLogModel.numberByTrueName)
        context.startActivity(intent)
    }

    @JvmStatic
    fun openCallDetailsActivity(number: String) {
        val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        val response = apiInterface.getNumberDetails(number, "92").execute()
        val name = if (response.body()?.status == true) {
            val data = response.body()?.data
            data?.name

        } else null

        openCallDetails(number, name)

    }

    private fun openCallDetails(number: String, name: String? = null) {
        val intent =
            Intent(context, CallDetails::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("name", name)
        intent.putStringArrayListExtra("numbers", arrayListOf(number))
        context.startActivity(intent)
    }

/* @JvmStatic
 fun doFacebookLogin(activity: SplashActivity) {
     val loginManager = LoginManager.getInstance()
     loginManager.registerCallback(fbCallbackManager, object : FacebookCallback<LoginResult> {
         override fun onSuccess(result: LoginResult?) {
             //retrieve access token and use throughout app from fb sdk.
             getFacebookFriends()
         }

         override fun onError(error: FacebookException?) {
             error?.printStackTrace()
         }

         override fun onCancel() {

         }
     })
     loginManager.logInWithReadPermissions(activity, listOf("user_friends"))
 }*/

/*  @JvmStatic
  fun handleFacebookResult(requestCode: Int, resultCode: Int, data: Intent?) = data?.run {
      fbCallbackManager.onActivityResult(requestCode, resultCode, this)
  }*/

/* fun getFacebookFriends() {
     val accessToken = AccessToken.getCurrentAccessToken()

     val request = GraphRequest.newMyFriendsRequest(
         accessToken
     ) { _, response ->
         // Insert your code here
         Log.d("FBFriends", response.rawResponse)
     }
     request.parameters = Bundle().apply {
         putString("summary", "public_profile")
     }
     request.executeAsync()

 }*/

    @JvmStatic
    fun isContactName(name: String?): Boolean {
        return !name.isNullOrBlank()
    }

    /*  private fun String.isNameOnly(): Boolean {
          return matches("[a-zA-Z0-9 ]+".toRegex())
      }*/

    @JvmStatic
    fun callNumber(appCompatActivity: AppCompatActivity, number: String) {
        PermissionsUtil.checkCallAndReadPhoneNumbersPermission(appCompatActivity) {
            val intent = Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:$number")
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }

    @JvmStatic
    fun openDialer(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    @JvmStatic
    fun openSmsApp(number: String) {
        val uri = Uri.parse("smsto:$number")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("sms_body", "")
        context.startActivity(intent)
    }


    @JvmStatic
    fun makeWhatsAppCall(name: String?, videoCall: Boolean) {

        if (!isWhatsAppInstalled() || name.isNullOrBlank())
            return

        var type = "vnd.android.cursor.item/vnd.com.whatsapp.voip.call"
        if (videoCall)
            type = "vnd.android.cursor.item/vnd.com.whatsapp.video.call"

        AppAsyncWorker.getWhatsAppUserId(name, type) {
            if (it != -1L) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //  intent.setPackage("com.whatsapp")
                intent.setDataAndType(Uri.parse("content://com.android.contacts/data/$it"), type)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "$name does not uses WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isWhatsAppInstalled(): Boolean {
        val whatsAppPackage = "com.whatsapp"
        if (!isAppInstalled(whatsAppPackage)) {
            Toast.makeText(context, "WhatsApp not Installed.", Toast.LENGTH_SHORT).show()
            val uri = Uri.parse("market://details?id=$whatsAppPackage")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(goToMarket)
            return false
        }

        return true
    }

    @JvmStatic
    fun openWhatsAppChat(number: String) {
        //   val whatsAppPackage = "com.whatsapp"
        val numberWithCountryCodeNoPlus = number.replace(" ", "").removePrefix("+")
        var fixedNumber = numberWithCountryCodeNoPlus
        if (!numberWithCountryCodeNoPlus.startsWith("92"))
            fixedNumber = "92${numberWithCountryCodeNoPlus.substring(1)}"
        // val sendIntent = Intent("$whatsAppPackage.Conversation")
        //  sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //  sendIntent.`package` = whatsAppPackage  // by not setting this option user will also get option in system picker for whatsapp business as well.
        sendIntent.data = Uri.parse("http://api.whatsapp.com/send?phone=$fixedNumber")
        // sendIntent.putExtra("jid", "$fixedNumber@s.whatsapp.net")
        context.startActivity(sendIntent)


    }

    //  private var provider: LocationGooglePlayServicesProvider? = null

    @JvmStatic
    fun shareLocationOnSms(
        appCompatActivity: AppCompatActivity?,
        phoneNumber: String?,
        name: String?
    ) {
        PermissionsUtil.checkLocationAndSendSMSPermission(appCompatActivity) {
            try {
                startLocation {

                    //val lastLocation = SmartLocation.with(context).location().lastLocation
                    sendLocationSMS(phoneNumber, name, it)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun shareLocation(
        appCompatActivity: AppCompatActivity?,
        onSuccess: (Location) -> Unit
    ) {
        PermissionsUtil.checkLocationPermission(appCompatActivity) {
            try {
                startLocation {
                    onSuccess(it)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startLocation(onSuccess: (Location) -> Unit) {

        val provider = LocationGooglePlayServicesProvider()
        provider.setCheckLocationSettings(true)
        val smartLocation = SmartLocation.Builder(context).logging(true).build()
        smartLocation.location(provider).start {
            onSuccess(it)
        }

        // Create some geofences
        /* val geoFence = GeofenceModel.Builder("1").setTransition(Geofence.GEOFENCE_TRANSITION_ENTER)
             .setLatitude(39.47453120000001).setLongitude(-0.358065799999963).setRadius(500f).build()
         smartLocation.geofencing().add(geoFence).start { }*/
    }

    @JvmStatic
    fun sendSMSToNumber(appCompatActivity: AppCompatActivity?, number: String, onSent: () -> Unit) {
        PermissionsUtil.checkSendSmsPermission(appCompatActivity) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
                    val response = apiInterface.invite("self")
                    //   Log.d("InviteAPI", response.status.toString())
                    val msg = response.msg
                    if (!msg.isNullOrBlank())
                        withContext(Dispatchers.Main.immediate) {
                            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                context.getSystemService(SmsManager::class.java)
                            else SmsManager.getDefault()
                            val smsBody = StringBuffer()
                            smsBody.append(Uri.parse(msg))
                            smsManager.sendTextMessage(
                                number,
                                null,
                                smsBody.toString(),
                                null,
                                null
                            )
                            onSent()
                        }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    @JvmStatic
    fun sendInvite(number: String, appCompatActivity: AppCompatActivity) {
        val dialog = Dialog(appCompatActivity)
        dialog.window?.run {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.setContentView(R.layout.invite_pop)
        val btnInvite = dialog.findViewById<Button>(R.id.btnInvite)
        val text = dialog.findViewById<TextView>(R.id.text)
        val txtNumber = dialog.findViewById<TextView>(R.id.txtNumber)
        text.text = context.getString(R.string.invite, number)
        txtNumber.text = number

        dialog.findViewById<ImageView>(R.id.cross).setOnClickListener {
            dialog.dismiss()
        }
        btnInvite.setOnClickListener {
            sendSMSToNumber(appCompatActivity, number) {
                Toast.makeText(
                    it.context,
                    context.getString(R.string.invite_sms_sent, number),
                    Toast.LENGTH_LONG
                ).show()
                dialog.dismiss()
            }
        }
        try {
            dialog.show()
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }
    }


    private fun sendLocationSMS(phoneNumber: String?, name: String?, currentLocation: Location) {
        val nameOrNumber = name ?: phoneNumber
        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            context.getSystemService(SmsManager::class.java)
        else SmsManager.getDefault()
        val smsBody = StringBuffer()
        val uri =
            "http://maps.google.com/maps?q=" + currentLocation.latitude + "," + currentLocation.longitude
        smsBody.append(Uri.parse(uri))
        smsManager.sendTextMessage(phoneNumber, null, smsBody.toString(), null, null)
        Toast.makeText(
            context,
            "Your location is successfully shared with $nameOrNumber",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun isAppInstalled(packageName: String): Boolean {
        val pm = context.packageManager
        var appInstalled = false
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            appInstalled = true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appInstalled
    }

}