package br.net.easify.tracker.view.dialogs


import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import br.net.easify.tracker.R

class UserDataDialog: DialogFragment() {

    private lateinit var mAlertDialog: AlertDialog
    private lateinit var mmView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        mmView = View.inflate(activity, R.layout.layout_user_data, null)
        isCancelable = false

        val builder: AlertDialog.Builder = AlertDialog.Builder(
            ContextThemeWrapper(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        )
            .setIcon(null)
            .setCancelable(false)
            .setView(mmView)

        mAlertDialog = builder.create()
        mAlertDialog!!.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)

        return mAlertDialog
    }

    fun show(manager: FragmentManager) {
        val ft = manager.beginTransaction()
        ft.add(this, this.javaClass.simpleName)
        ft.commitAllowingStateLoss()
    }
}