package br.net.easify.tracker.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.net.easify.tracker.R
import br.net.easify.tracker.databinding.ActivityLoginBinding
import br.net.easify.tracker.model.ErrorResponse
import br.net.easify.tracker.model.LoginBody
import br.net.easify.tracker.model.Token
import br.net.easify.tracker.view.main.MainActivity
import br.net.easify.tracker.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private var loginBody: LoginBody? = null
    private lateinit var dataBinding: ActivityLoginBinding

    private val tokensObserver = Observer<Token> {
        startMainActivity()
    }

    private val errorMessageObserver = Observer<ErrorResponse> { error: ErrorResponse ->
        error.let {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
    }

    private val loginObserver = Observer<LoginBody> { data: LoginBody ->
        data.let {
            loginBody = it
            dataBinding.loginBody = loginBody
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewModel.loginBody.observe(this, loginObserver)
        viewModel.tokens.observe(this, tokensObserver)
        viewModel.errorResponse.observe(this, errorMessageObserver)

        dataBinding.loginButton.setOnClickListener(View.OnClickListener {
            viewModel.login(this)
        })

        dataBinding.loginBody = this.loginBody
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