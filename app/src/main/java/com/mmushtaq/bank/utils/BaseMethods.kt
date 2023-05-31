package com.mmushtaq.bank.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.text.Editable
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.mmushtaq.bank.R
import com.mmushtaq.bank.remote.AppConstants
import com.mmushtaq.bank.remote.BaseApplication
import com.wang.avi.AVLoadingIndicatorView
import java.text.SimpleDateFormat
import java.util.*

class BaseMethods {
    companion object {
        private var indicatorView: AVLoadingIndicatorView? = null
        private var dialog: Dialog? = null


        fun progressdialog(context: Context) {
            dialog = Dialog(context)
            dialog?.setContentView(R.layout.myloader)
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            indicatorView = dialog?.findViewById(R.id.avi)
            startAnim()
            dialog?.show()
        }

        fun finishprogress() {
            stopAnim()
            if (dialog != null && dialog?.isShowing!!)
                dialog?.dismiss()
        }

        private fun startAnim() {
            indicatorView?.show()
            // or avi.smoothToShow();
        }

        private fun stopAnim() {
            indicatorView?.hide()
            // or avi.smoothToHide();
        }
        fun isLogin(): Boolean {
            return AppConstants.YES == com.mmushtaq.bank.remote.SharedPreferences.getSharedPreferences(AppConstants.KEY_SHARED_PREFERENCE_LOGGED, BaseApplication.getContext())
        }
        private val cache: MutableMap<String, Typeface> = Hashtable()
        fun isNullOrEmptyString(arg: String?): Boolean {
            return arg == null || arg.trim { it <= ' ' } == "" || arg.trim { it <= ' ' }.equals("null", ignoreCase = true)
        }

        fun isNullOrEmptyString(arg: Editable?): Boolean {
            return arg == null || arg.toString().trim { it <= ' ' } == "" || arg.toString().trim { it <= ' ' }.equals("null", ignoreCase = true)
        }

        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            return sdf.format(Date())
        }
        fun getCurrentTime(): String {
            val sdf = SimpleDateFormat("hh:mm:ss a")
            return sdf.format(Date())
        }



        fun hideKeyboard(activity: Activity) {
            try {
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            } catch (e: Exception) {
//                Log.e("hideKeyboard", e.message.toString())
            }
        }

        fun haveNetworkConnection(context : Context): Boolean {
            var haveConnectedWifi = false
            var haveConnectedMobile = false
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val netInfo = cm!!.allNetworkInfo
            for (ni in netInfo) {
                if (ni.typeName.equals("WIFI", ignoreCase = true)) if (ni.isConnected) haveConnectedWifi = true
                if (ni.typeName.equals("MOBILE", ignoreCase = true)) if (ni.isConnected) haveConnectedMobile = true
            }
            return haveConnectedWifi || haveConnectedMobile
        }
    }

}