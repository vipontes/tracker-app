package br.net.easify.tracker.view.login

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.net.easify.tracker.R
import br.net.easify.tracker.database.model.DbToken
import br.net.easify.tracker.databinding.ActivityLoginBinding
import br.net.easify.tracker.model.Response
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.model.User
import br.net.easify.tracker.view.main.MainActivity
import br.net.easify.tracker.viewmodel.LoginViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginBody: LoginBody
    private lateinit var dataBinding: ActivityLoginBinding

    private val tokensObserver = Observer<Token> {

        var tokenLocal = DbToken(it.token, it.refreshToken)
        viewModel.saveTokens(tokenLocal)
        viewModel.getUserFromToken()
    }

    private val errorMessageObserver = Observer<Response> { error: Response ->
        error.let {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            dataBinding.spinner.visibility = View.GONE
            dataBinding.loginButton.visibility = View.VISIBLE
        }
    }

    private val loginObserver = Observer<LoginBody> { data: LoginBody ->
        data.let {
            loginBody = it
            dataBinding.loginBody = loginBody
        }
    }

    private val userObserver = Observer<User> { data: User ->
        data.let {
            viewModel.saveLoggedUser(data)
            startMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Dexter.withActivity(this@LoginActivity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {

                }
            }).check()

        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        if  (viewModel.getLoggedUser() != null) {
            startMainActivity()
            return
        }

        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewModel.loginBody.observe(this, loginObserver)
        viewModel.loggedUser.observe(this, userObserver)
        viewModel.tokens.observe(this, tokensObserver)
        viewModel.errorResponse.observe(this, errorMessageObserver)

        dataBinding.loginButton.setOnClickListener(View.OnClickListener {
            if ( viewModel.validate() ) {
                dataBinding.spinner.visibility = View.VISIBLE
                dataBinding.loginButton.visibility = View.GONE
                viewModel.login()
            }
        })
    }

    private fun startMainActivity() {
        val mainActivity = Intent(this, MainActivity::class.java)
        mainActivity.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mainActivity)
        finish()
    }
}