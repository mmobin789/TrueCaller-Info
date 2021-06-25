package com.magicbio.truename.call

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdView
import com.magicbio.truename.R
import com.magicbio.truename.fragments.background.AppAsyncWorker.loadLastCallLogByNumber
import com.magicbio.truename.models.GetNumberResponse
import com.magicbio.truename.retrofit.ApiClient
import com.magicbio.truename.retrofit.ApiInterface
import com.magicbio.truename.utils.AdUtils
import com.magicbio.truename.utils.ContactUtils
import com.magicbio.truename.utils.ContactUtils.isContactName
import com.magicbio.truename.utils.ContactUtils.openWhatsAppChat
import com.magicbio.truename.utils.ContactUtils.shareLocationOnSms
import io.pixel.Pixel
import io.pixel.config.PixelConfiguration.setLoggingEnabled
import io.pixel.config.PixelOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CallsListener : PhoneStateListener() {

    private lateinit var context: Context
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var windowManager: WindowManager
    private lateinit var windowParams: WindowManager.LayoutParams
    private var beforeCallPopupView: View? = null
    private var afterCallPopupView: View? = null
    private val apiInterface by lazy {
        ApiClient.getClient().create(
            ApiInterface::
            class.java
        )
    }

    companion object {
        //  private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var lastNumber = ""
        private var beforeCallPopUpShown = false
        private var afterCallPopUpShown = false
    }

    fun setContext(context: Context) {
        this.context = context
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        createWindowParams()
    }

    private fun createWindowParams() {
        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        windowParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        windowParams.x = Gravity.CENTER
        windowParams.y = Gravity.CENTER
    }

    private fun showBeforeCallPopUpWindow(phoneNumber: String) {
        if (beforeCallPopUpShown)
            return
        beforeCallPopupView = layoutInflater.inflate(R.layout.incoming_call_pop, null)
        showInfoOnPopUpBeforeCall(phoneNumber)
        addOnTouchListener(beforeCallPopupView)
        windowManager.addView(beforeCallPopupView, windowParams)
        beforeCallPopUpShown = true
    }

    private fun showAfterCallPopUpWindow(phoneNumber: String) {
        if (afterCallPopUpShown)
            return
        afterCallPopupView = layoutInflater.inflate(R.layout.after_call_pop, null)
        showInfoOnPopUpAfterCall(phoneNumber)
        addOnTouchListener(afterCallPopupView)
        windowManager.addView(afterCallPopupView, windowParams)
        afterCallPopUpShown = true
    }

    private fun showInfoOnPopUpBeforeCall(number: String) {
        val view = beforeCallPopupView!!
        val ivAd = view.findViewById<ImageView>(R.id.ivAd)
        val adView1 = view.findViewById<AdView>(R.id.adView)
        val adView2 = view.findViewById<AdView>(R.id.adView2)
        val txtLastCall = view.findViewById<TextView>(R.id.txtLastCall)
        val txtName = view.findViewById<TextView>(R.id.txtName)
        val txtNumber = view.findViewById<TextView>(R.id.txtNumber)
        val cross = view.findViewById<ImageView>(R.id.cross)
        AdUtils.loadBannerAd(adView1)
        AdUtils.loadBannerAd(adView2)
        cross.setOnClickListener {
            windowManager.removeView(view)
            beforeCallPopUpShown = false
        }

        loadLastCallLogByNumber(number)?.let { callLog ->
            txtName.text = if (isContactName(callLog.name))
                callLog.name
            else callLog.phNumber

            txtLastCall.text = getDateFormatted(callLog.callDate)

            txtNumber.text = number
        }

        getNumberDetails(number, txtName, txtNumber, ivAd, adView2)

    }

    private fun showInfoOnPopUpAfterCall(number: String) {
        val view = afterCallPopupView!!
        val ivAd = view.findViewById<ImageView>(R.id.ivAd)
        val adView1 = view.findViewById<AdView>(R.id.adView)
        val adView2 = view.findViewById<AdView>(R.id.adView2)
        val txtNumber = view.findViewById<TextView>(R.id.txtNumber)
        val txtLastCall = view.findViewById<TextView>(R.id.txtLastCall)
        val txtName = view.findViewById<TextView>(R.id.txtName)
        val btnCall = view.findViewById<ImageView>(R.id.btnCall)
        val btnInvite = view.findViewById<ImageView>(R.id.btnInvite)
        val btnSave = view.findViewById<ImageView>(R.id.btnSave)
        val btnMessage = view.findViewById<ImageView>(R.id.btnMessage)
        val ivWhatsApp = view.findViewById<ImageView>(R.id.ivWta)
        val ivLoc = view.findViewById<Button>(R.id.ivLocation)
        val cross = view.findViewById<ImageView>(R.id.cross)
        AdUtils.loadBannerAd(adView1)
        AdUtils.loadBannerAd(adView2)

        cross.setOnClickListener {
            windowManager.removeView(view)
            afterCallPopUpShown = false
        }


        btnInvite.setOnClickListener { v ->
            ContactUtils.sendInvite(number, v.context)
        }

        btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        btnMessage.setOnClickListener {
            val uri = Uri.parse("smsto:$number")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("sms_body", "The SMS text")
            context.startActivity(intent)
        }

        ivWhatsApp.setOnClickListener {
            openWhatsAppChat(number)
        }

        ivLoc.setOnClickListener {
            shareLocationOnSms(
                number,
                txtName.text.toString()
            )
        }


        val name = loadLastCallLogByNumber(number)?.let { callLog ->
            val name = callLog.name
            txtName.text = if (isContactName(name))
                name
            else callLog.phNumber

            txtLastCall.text = getDateFormatted(callLog.callDate)

            txtNumber.text = number

            name

        }

        btnSave.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.type = ContactsContract.Contacts.CONTENT_TYPE
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, number)
            intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
            context.startActivity(intent)
        }

        getNumberDetails(number, txtName, txtNumber, ivAd, adView2)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addOnTouchListener(v: View?) {
        v?.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = windowParams.x
                        initialY = windowParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        // lastActionDownX = event.getX();
                        // lastActionDownY = event.getY();
                        // add recycle bin when moving crumpled paper
                        //addRecycleBinView();
                        return true
                    }
                    MotionEvent.ACTION_UP ->
                        //  long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        //   if (clickDuration < MAX_CLICK_DURATION) {
                        //click event has occurred

                        // }


                        //  int centerOfScreenByX = windowWidth / 2;

                        // remove crumpled paper when the it is in the recycle bin's area


                        // always remove recycle bin ImageView when paper is dropped
                        return true
                    MotionEvent.ACTION_MOVE -> {
                        // move paper ImageView
                        windowParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        windowParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(v, windowParams)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun getNumberDetails(
        number: String,
        txtName: TextView,
        txtNumber: TextView,
        ivAd: ImageView,
        adView2: AdView
    ) {
        apiInterface.getNumberDetails(number, "92")
            .enqueue(object : Callback<GetNumberResponse?> {
                override fun onResponse(
                    call: Call<GetNumberResponse?>,
                    response: Response<GetNumberResponse?>
                ) {
                    if (response.body() != null && response.body()!!.status) {
                        val data = response.body()!!.data
                        txtName.text = data.name
                        setLoggingEnabled(true)
                        if (data.type == "yes") {
                            Pixel.load(
                                data.image,
                                PixelOptions.Builder()
                                    .setPlaceholderResource(R.drawable.sms_connect_ad)
                                    .build(),
                                ivAd
                            )
                        } else {
                            adView2.visibility = View.VISIBLE
                        }
                        txtNumber.text = data.number
                    }
                }

                override fun onFailure(call: Call<GetNumberResponse?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }


/*    private fun removePopUpViews() {
        beforeCallPopupView?.let {
            if (beforeCallPopUpShown) {
                windowManager.removeView(it)
                beforeCallPopUpShown = false
            }
        }
        afterCallPopupView?.let {
            if (afterCallPopUpShown) {
                windowManager.removeView(it)
                afterCallPopUpShown = false
            }
        }

        beforeCallPopupView = null
        afterCallPopupView = null
    }*/

    override fun onCallStateChanged(state: Int, phoneNumber: String) {

        try {
            //  removePopUpViews()

            if (phoneNumber.isNotBlank())
                lastNumber = phoneNumber

            if (lastNumber.isBlank())
                return

            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d("IncomingCall", "Incoming")
                    showBeforeCallPopUpWindow(lastNumber)
                }

                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    Log.d("OutgoingCall", "Outgoing")
                    showBeforeCallPopUpWindow(lastNumber)

                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    showAfterCallPopUpWindow(lastNumber)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        // lastState = state
    }

    private fun getDateFormatted(milliSeconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}