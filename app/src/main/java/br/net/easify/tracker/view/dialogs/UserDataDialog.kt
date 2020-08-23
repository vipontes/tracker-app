package br.net.easify.tracker.view.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.net.easify.tracker.R
import br.net.easify.tracker.databinding.LayoutUserDataBinding
import br.net.easify.tracker.repositories.database.model.SqliteUser
import br.net.easify.tracker.viewmodel.UserDataViewModel


class UserDataDialog : DialogFragment() {

    private lateinit var viewModel: UserDataViewModel
    private lateinit var theDialog: AlertDialog
    private lateinit var dataBinding: LayoutUserDataBinding

    private val userObserver = Observer<SqliteUser> {
        dataBinding.userData = it
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        dataBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_user_data,
                null,
                false)

        isCancelable = false

        viewModel = ViewModelProviders.of(this).get(UserDataViewModel::class.java)
        viewModel.userData.observe(this, userObserver)

        val builder: AlertDialog.Builder = AlertDialog.Builder(
            ContextThemeWrapper(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        ).setIcon(null).setView(dataBinding.root)

        theDialog = builder.create()
        theDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dataBinding.saveButton.setOnClickListener(View.OnClickListener {
            theDialog.dismiss()
        })

        dataBinding.closeButton.setOnClickListener(View.OnClickListener {
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