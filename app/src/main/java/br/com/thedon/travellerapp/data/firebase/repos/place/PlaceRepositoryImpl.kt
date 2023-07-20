package br.com.thedon.travellerapp.data.firebase.repos.place

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.dataObjects
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PlaceRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) :
    PlacesRepository {
    companion object {
        private const val PLACE_COLLECTION = "places"
        private const val USER_ID_FIELD = "userId"
        private const val BUCKET = "places"
    }

    val storageReference = storage.getReference(BUCKET)

    val localPlaces: List<Place> = mutableListOf()

    override fun getPlaces(): Flow<List<Place>> {
        val userId = auth.currentUser?.uid

        return firestore.collection(PLACE_COLLECTION).whereEqualTo(USER_ID_FIELD, userId)
            .dataObjects()
    }

    override val getUserPlaces = callbackFlow<Resources<List<Place>>> {
        val userId = auth.currentUser?.uid

        val snapshotListener = EventListener<QuerySnapshot> { snapshot, e ->
            val response = if (snapshot != null) {
                val places = snapshot.toObjects(Place::class.java)
                Resources.Success(data = places)
            } else {
                Resources.Error(throwable = e?.cause)
            }

            trySend(response)
        }

        val registration =
            firestore.collection(PLACE_COLLECTION).whereEqualTo(USER_ID_FIELD, userId)
                .addSnapshotListener(snapshotListener)

        awaitClose {
            registration.remove()
        }

    }

    override suspend fun getPlace(id: String): Resources<Place> {
        try {


            val place = firestore.collection(PLACE_COLLECTION).document(id).get().await()
                .toObject(Place::class.java)

            return Resources.Success(data = place)
        } catch (e: Throwable) {
            return Resources.Error(e.cause)
        }
    }

    override suspend fun savePlace(place: Place) {
        val userId = auth.currentUser?.uid ?: return

        var newPlace = place.copy(userId = userId)
        if (!place.photoUrl.startsWith("gs://")) {
            val photoURI = Uri.parse(place.photoUrl)
            val photoRef =
                storageReference.child("image/${newPlace.userId}/${photoURI.lastPathSegment}")

            val result = photoRef.putFile(photoURI).await()

            if (result.task.isSuccessful) {
                newPlace = newPlace.copy(photoUrl = photoRef.toString())
            }

        }

        if (newPlace.id.isNullOrBlank()) {
            val docRef = firestore.collection(PLACE_COLLECTION).document()

            docRef.set(newPlace).await()
        } else {
            firestore.collection(PLACE_COLLECTION).document(newPlace.id)
                .set(newPlace, SetOptions.merge()).await()
        }
    }

    override suspend fun removePlace(id: String) {
        firestore.collection(PLACE_COLLECTION).document(id).delete().await()
    }
}