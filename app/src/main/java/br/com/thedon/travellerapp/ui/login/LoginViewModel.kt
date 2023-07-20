package br.com.thedon.travellerapp.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import br.com.thedon.travellerapp.R
import br.com.thedon.travellerapp.data.firebase.auth.FirebaseAuthManager
import br.com.thedon.travellerapp.data.firebase.repos.user.UserRepository
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(private val userRepository: UserRepository) : ViewModel(), FirebaseAuthManager {
    private val _isUserLogged = MutableStateFlow<Boolean>(userRepository.isUserLoggedIn())
    val isUserLogged = _isUserLogged.asStateFlow()

    //Used to perform appropriate action based on the login result
    private val _authResultCode = MutableStateFlow(AuthResultCode.NOT_APPLICABLE)
    val authResultCode = _authResultCode.asStateFlow()

    override fun buildLoginIntent(): Intent {
        return AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(
                listOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GitHubBuilder().build()
                )
            )
            .enableAnonymousUsersAutoUpgrade()
            .setLogo(R.mipmap.ic_launcher)
            .build()
    }

    override fun onLoginResult(result: FirebaseAuthUIAuthenticationResult) {
        val response: IdpResponse? = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {
            _isUserLogged.value = true
            _authResultCode.value = AuthResultCode.OK
            return
        }


        val userPressedBackButton = (response == null)
        if (userPressedBackButton) {
            _authResultCode.value = AuthResultCode.CANCELLED
            return
        }

        when (response?.error?.errorCode) {
            ErrorCodes.NO_NETWORK -> {
                _authResultCode.value = AuthResultCode.NO_NETWORK
            }
            else -> {
                _authResultCode.value = AuthResultCode.ERROR
            }


        }
    }

    override fun signOut() {
        userRepository.signOut()
        _isUserLogged.value = false
    }

}