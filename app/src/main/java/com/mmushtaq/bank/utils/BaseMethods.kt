package com.mmushtaq.bank.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Editable
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.mmushtaq.bank.R
import com.mmushtaq.bank.remote.BaseApplication
import com.wang.avi.AVLoadingIndicatorView
import java.text.SimpleDateFormat
import java.util.*

object BaseMethods {
    private var indicatorView: AVLoadingIndicatorView? = null
    private var dialog: Dialog? = null


    fun showProgressDialog(context: Context) {
        try {
            dialog = Dialog(context)
            dialog?.setContentView(R.layout.myloader)
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            indicatorView = dialog?.findViewById(R.id.avi)
            dialog?.show()
            indicatorView?.show()
        } catch (_: Exception) {
        }
    }

    fun hideProgressDialog() {
        stopAnim()
        if (dialog?.isShowing == true)
            dialog?.dismiss()
    }

    private fun stopAnim() {
        indicatorView?.hide()
        // or avi.smoothToHide();
    }

    fun isLogin(activity: Activity): Boolean {
        return AppConstants.YES == com.mmushtaq.bank.remote.SharedPreferences.getSharedPreferences(
            AppConstants.KEY_SHARED_PREFERENCE_LOGGED, activity.applicationContext
        )
    }

    fun isNullOrEmptyString(arg: String?): Boolean {
        return arg == null || arg.trim { it <= ' ' } == "" || arg.trim { it <= ' ' }
            .equals("null", ignoreCase = true)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm:ss a")
        return sdf.format(Date())
    }


    fun hideKeyboard(activity: Activity) {
        try {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (_: Exception) {
        }
    }

    fun haveNetworkConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}