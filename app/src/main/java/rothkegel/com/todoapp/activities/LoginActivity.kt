package rothkegel.com.todoapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import kotlinx.android.synthetic.main.login_activity.*
import org.jetbrains.anko.toast
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.api.connector.utils.User

class LoginActivity : ToDoAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        setMailTextChangedListener()
        setPasswordTextChangedListener()
        setButtonClickListener()
        checkInternetConnection()
    }

    private fun checkInternetConnection() {
        showInternetCheckLoading(true)
        hasInternetConnection().subscribe { hasInternet ->
            saveInternetStatus(hasInternet)

            if (!hasInternet) {
                goToToDoList()
            }
            showInternetCheckLoading(false)
        }
    }

    private fun setButtonClickListener() {
        login_action.setOnClickListener {
            loginActionClicked()
        }
    }

    private fun loginActionClicked() {

        if (TextUtils.isEmpty(login_email_address.text)) {
            toast(getString(R.string.error_empty_mail_message))
            return
        }
        if (TextUtils.isEmpty(login_password.text)) {
            toast(getString(R.string.error_empty_password_message))
            return
        }

        if (login_password.text.length != 6) {
            toast(getString(R.string.error_wrong_password_length_message))
            return
        }

        if (isInvalidMail()) {
            showError(getString(R.string.error_invalid_mail_message), true)
            return
        }

        val user = User(login_email_address.text.toString(), pwd = login_password.text.toString())
        showLoginLoading(true)
        loginUser(user)
    }

    private fun showError(errorMessage: String, show: Boolean) {
        login_error_message.text = errorMessage

        when (show) {
            true -> login_error_message.visibility = View.VISIBLE
            false -> login_error_message.visibility = View.GONE
        }
    }

    private fun isInvalidMail() = !Patterns.EMAIL_ADDRESS.matcher(login_email_address.text).matches()

    private fun setPasswordTextChangedListener() {
        login_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable) {
                onTextFieldChanged()
            }
        })
    }

    private fun setMailTextChangedListener() {
        login_email_address.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable) {
                onTextFieldChanged()
            }
        })
    }

    private fun onTextFieldChanged() {
        showError("", false)
        login_action.isEnabled = login_email_address.text.isNotEmpty() && login_password.text.isNotEmpty()
    }

    override fun onLoggedInUser(loggedIn: Boolean?) {
        super.onLoggedInUser(loggedIn)
        if (loggedIn == null) {
            return
        }

        if (loggedIn) {
            goToToDoList()
        } else {
            showError(getString(R.string.error_wrong_credentials_message), true)
        }
        showLoginLoading(false)
    }

    private fun goToToDoList() {
        val intent = Intent(this, ToDoListActivity::class.java)
        this.startActivity(intent)
    }


    private fun showInternetCheckLoading(loading: Boolean) {
        if (loading) {
            login_welcome_message.visibility = View.GONE
            login_email_address.visibility = View.GONE
            login_password.visibility = View.GONE
            login_error_message.visibility = View.GONE

            login_progress_bar.visibility = View.VISIBLE
            login_action.visibility = View.GONE
        } else {
            login_welcome_message.visibility = View.VISIBLE
            login_email_address.visibility = View.VISIBLE
            login_password.visibility = View.VISIBLE
            login_error_message.visibility = View.GONE

            login_progress_bar.visibility = View.GONE
            login_action.visibility = View.VISIBLE
        }
    }

    private fun showLoginLoading(loading: Boolean) {
        if (loading) {
            login_progress_bar.visibility = View.VISIBLE
            login_action.visibility = View.GONE
        } else {
            login_progress_bar.visibility = View.GONE
            login_action.visibility = View.VISIBLE
        }
    }
}
