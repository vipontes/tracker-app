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
import br.net.easify.tracker.R
import br.net.easify.tracker.repositories.database.model.DbUser
import br.net.easify.tracker.databinding.FragmentSettingsBinding
import br.net.easify.tracker.helpers.CustomAlertDialog
import br.net.easify.tracker.view.login.LoginActivity
import br.net.easify.tracker.viewmodel.SettingsViewModel


class SettingsFragment : Fragment() {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var dataBinding: FragmentSettingsBinding
    private lateinit var userData: DbUser
    private var alertDialog: AlertDialog? = null

    private val userDataObserver = Observer<DbUser> {
        if (it != null) {
            userData = it
            dataBinding.userData = userData
        } else {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        viewModel.userRepository.userData.observe(viewLifecycleOwner, userDataObserver)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeButtons()
    }

    private fun initializeButtons() {
        dataBinding.editProfileButton.setOnClickListener(View.OnClickListener {

        })

        dataBinding.logout.setOnClickListener(View.OnClickListener {
            alertDialog = CustomAlertDialog.show(requireContext(), getString(R.string.logout),
                getString(R.string.logout_confirmation),
                getString(R.string.yes), View.OnClickListener {
                    alertDialog!!.dismiss()
                    viewModel.logout()
                },
                getString(R.string.no),
                View.OnClickListener { alertDialog!!.dismiss() }
            )
        })
    }
}