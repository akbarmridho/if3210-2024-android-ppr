package com.informatika.bondoman.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.model.repository.login.LoginRepositoryImpl
import com.informatika.bondoman.model.Resource

import com.informatika.bondoman.R
import com.informatika.bondoman.model.repository.login.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult


    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {

            when (val result = loginRepository.login(username, password)) {
                is Resource.Success -> {
                    _loginResult.postValue(LoginResult(jwtToken = result.data))
                }
                is Resource.Error -> {
                    _loginResult.postValue(LoginResult(error = R.string.login_failed))
                }

                is Resource.Loading -> Unit // Do nothing
            }
        }
    }

    fun getEmail() : String {
        return loginRepository.getEmail();
    }

    suspend fun logout() {
        loginRepository.logout();
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 8
    }
}