package br.com.thedon.travellerapp.data.firebase.repos.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class UserRepositoryImpl(private val auth: FirebaseAuth): UserRepository {

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser;
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override val isSignedIn = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }

        auth.addAuthStateListener(authStateListener)

        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    override fun signOut() {
        auth.signOut()
    }
}