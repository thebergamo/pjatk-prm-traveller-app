package br.com.thedon.travellerapp.data.firebase.repos.user

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): FirebaseUser?

    fun isUserLoggedIn(): Boolean

    val isSignedIn: Flow<Boolean>

    fun signOut()
}