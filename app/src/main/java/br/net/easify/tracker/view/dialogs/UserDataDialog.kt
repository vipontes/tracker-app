package br.net.easify.tracker.view.dialogs


import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import br.net.easify.tracker.R
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.layout_user_data.*

class UserDataDialog: DialogFragment() {

    private lateinit var theDialog: AlertDialog
    private lateinit var layoutView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        layoutView = View.inflate(activity, R.layout.layout_user_data, null)
        isCancelable = false

        val saveButton = layoutView.findViewById<MaterialButton>(R.id.saveButton)
        val closeButton = layoutView.findViewById<ImageButton>(R.id.closeButton)

        val builder: AlertDialog.Builder = AlertDialog.Builder(
            ContextThemeWrapper(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        )
            .setIcon(null)
            .setCancelable(true)
            .setView(layoutView)

        theDialog = builder.create()
        theDialog!!.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)

        saveButton.setOnClickListener(View.OnClickListener {
            theDialog.dismiss()
        })

        closeButton.setOnClickListener(View.OnClickListener {
            theDialog.dismiss()
        })

        return theDialog
    }

    fun show(manager: FragmentManager) {
        val ft = manager.beginTransaction()
        ft.add(this, this.javaClass.simpleName)
        ft.commitAllowingStateLoss()
    }
}