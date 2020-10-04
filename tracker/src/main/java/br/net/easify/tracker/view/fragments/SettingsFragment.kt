package br.net.easify.tracker.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.net.easify.database.model.SqliteUser
import br.net.easify.tracker.R
import br.net.easify.tracker.databinding.FragmentSettingsBinding
import br.net.easify.tracker.helpers.CustomAlertDialog
import br.net.easify.tracker.view.dialogs.UserDataDialog
import br.net.easify.tracker.view.login.LoginActivity
import br.net.easify.tracker.viewmodel.SettingsViewModel


class SettingsFragment : Fragment(), UserDataDialog.OnSaveListener {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var dataBinding: FragmentSettingsBinding
    private lateinit var userData: SqliteUser
    private var alertDialog: AlertDialog? = null

    private val userDataObserver = Observer<SqliteUser> {
        if (it != null) {
            userData = it
            dataBinding.userData = userData
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_settings,
            container,
            false
        )

        viewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        viewModel.userData.observe(
            viewLifecycleOwner,
            userDataObserver
        )

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeButtons()
    }

    private fun initializeButtons() {
        dataBinding.editProfileButton.setOnClickListener(View.OnClickListener {

            val dialog = UserDataDialog()
            dialog.allowEnterTransitionOverlap = false
            dialog.show(parentFragmentManager, this)
        })

        dataBinding.logout.setOnClickListener(View.OnClickListener {
            alertDialog = CustomAlertDialog.show(requireContext(),
                getString(R.string.logout),
                getString(R.string.logout_confirmation),
                getString(R.string.yes),
                View.OnClickListener {
                    alertDialog!!.dismiss()
                    viewModel.logout()
                    getLoginActivity()

                },
                getString(R.string.no),
                View.OnClickListener { alertDialog!!.dismiss() }
            )
        })
    }

    private fun getLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        activity?.startActivity(intent)
        activity?.finish()
    }

    override fun onSave() {
        viewModel.getLoggedUser()
    }
}