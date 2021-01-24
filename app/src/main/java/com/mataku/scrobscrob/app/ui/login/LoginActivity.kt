package com.mataku.scrobscrob.app.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mataku.scrobscrob.R
import com.mataku.scrobscrob.app.model.RxEventBus
import com.mataku.scrobscrob.app.ui.settings.SettingsActivity
import com.mataku.scrobscrob.core.api.LastFmApiClient
import com.mataku.scrobscrob.core.api.repository.MobileSessionRepository
import com.mataku.scrobscrob.core.entity.Track
import com.mataku.scrobscrob.core.entity.UpdateNowPlayingEvent
import com.mataku.scrobscrob.databinding.ActivityLoginBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * A login screen that offers login via email/password.
 */
@ExperimentalCoroutinesApi
class LoginActivity : AppCompatActivity(), LoginViewCallback {

    private lateinit var userNameView: AutoCompleteTextView
    private lateinit var passwordView: EditText
    private lateinit var progressView: View
    private lateinit var loginFormView: View

    private val repository = MobileSessionRepository(LastFmApiClient(this))
    private lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        // Set up the login form.
        userNameView = binding.userName
        passwordView = binding.password
        binding.password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        val userNameSignInButton = binding.emailSignInButton
        userNameSignInButton.setOnClickListener { attemptLogin() }

        loginFormView = binding.loginForm
        progressView = binding.loginProgress
        loginPresenter = LoginPresenter(isEnabledReadNotification(), this, repository)
    }

    override fun showError() {
        showProgress(false)
        Toast.makeText(this, "Invalid UserName or password!", Toast.LENGTH_LONG).show()
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Reset errors.
        userNameView.error = null
        passwordView.error = null

        // Store values at the time of the login attempt.
        val email = userNameView.text.toString()
        val password = passwordView.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(password)) {
            passwordView.error = getString(R.string.error_field_required)
            focusView = passwordView
            cancel = true
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.error = getString(R.string.error_invalid_password)
            focusView = passwordView
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            userNameView.error = getString(R.string.error_field_required)
            focusView = userNameView
            cancel = true
        } else if (!isUserNameValid(email)) {
            userNameView.error = getString(R.string.error_invalid_user_name)
            focusView = userNameView
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            loginPresenter.auth(email, password)
        }
    }

    private fun isUserNameValid(userName: String): Boolean {
        return userName.isNotEmpty()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotEmpty()
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        loginFormView.visibility = if (show) View.GONE else View.VISIBLE
        loginFormView.animate().setDuration(shortAnimTime.toLong()).alpha(
            (if (show) 0 else 1).toFloat()
        ).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                loginFormView.visibility = if (show) View.GONE else View.VISIBLE
            }
        })

        progressView.visibility = if (show) View.VISIBLE else View.GONE
        progressView.animate().setDuration(shortAnimTime.toLong()).alpha(
            (if (show) 1 else 0).toFloat()
        ).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                progressView.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onDestroy() {
        loginPresenter.dispose()
        super.onDestroy()
    }

    override fun setSessionInfo(sessionKey: String, userName: String) {
        val data = getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = data.edit()
        editor.putString("SessionKey", sessionKey)
        editor.putString("UserName", userName)
        editor.apply()

        showProgress(false)

        loginPresenter.backToSettingsWhenLoggedIn(true, sessionKey)
    }

    override fun showSuccessMessage() {
        Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show()
        RxEventBus.send(UpdateNowPlayingEvent(dummyTrack()))
    }

    override fun showMessageToAllowAccessToNotification() {
        Toast.makeText(
            this,
            "Allow Notification access to AppleMusicNotificationService",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun backToSettingsActivity() {
        finish()
        val intent = Intent(applicationContext, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun focusOnPasswordView() {
        passwordView.error = getString(R.string.error_incorrect_password)
        passwordView.requestFocus()
    }

    private fun isEnabledReadNotification(): Boolean {
        val contentResolver = contentResolver
        val rawListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        if (rawListeners == null || rawListeners == "") {
            return false
        } else {
            val listeners =
                rawListeners.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            listeners
                .filter { it.startsWith(packageName) }
                .forEach { return true }
        }
        return false
    }

    private fun dummyTrack(): Track {
        return Track(
            getString(R.string.label_not_playing_message),
            getString(R.string.label_now_playing),
            getString(R.string.label_not_playing)
        )
    }
}
