package com.informatika.bondoman.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.ActivityLoginBinding
import com.informatika.bondoman.viewmodel.JWTViewModel
import com.informatika.bondoman.viewmodel.login.LoginFormState
import com.informatika.bondoman.viewmodel.login.LoginViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : NetworkAwareActivity() {
    private val loginViewModel: LoginViewModel by viewModel()

    private lateinit var binding: ActivityLoginBinding
    private val jwtViewModel: JWTViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etUsername = binding.etUsername
        val etPassword = binding.etPassword
        val etLogin = binding.btnSignIn
        val pbLoading = binding.pbLoading

        loginViewModel.loginFormState.asFlow()
            .combine(this.connectivityViewModel.getConnectivityFlow()) { a: LoginFormState, b: Boolean ->
                Pair(
                    a,
                    b
                )
            }.asLiveData().observe(this@LoginActivity, Observer {
            val loginState = it.first ?: return@Observer
            val isOnline = it.second

            // disable login button unless both username / password is valid
            etLogin.isEnabled = loginState.isDataValid && isOnline

            if (loginState.usernameError != null) {
                etUsername.error = getString(loginState.usernameError)
            }

            if (loginState.passwordError != null) {
                etPassword.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            pbLoading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }

            if (loginResult.jwtToken != null) {
                runBlocking {
                    jwtViewModel.jwtManager.saveToken(loginResult.jwtToken)
                }
                updateUiWithUser()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            setResult(Activity.RESULT_OK)
        })

        etUsername.afterTextChanged {
            loginViewModel.loginDataChanged(
                etUsername.text.toString(),
                etPassword.text.toString()
            )
        }

        etPassword.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    etUsername.text.toString(),
                    etPassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            etUsername.text.toString(),
                            etPassword.text.toString()
                        )
                }
                false
            }

            etLogin.setOnClickListener {
                pbLoading.visibility = View.VISIBLE
                loginViewModel.login(etUsername.text.toString(), etPassword.text.toString())
            }
        }

    }

    private fun updateUiWithUser() {
        val welcome = getString(R.string.welcome)
        Toast.makeText(
            applicationContext,
            "$welcome to Bondoman",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

}