package br.com.thedon.travellerapp.data.firebase.repos.place

import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun getPlaces(): Flow<List<Place>>
    val getUserPlaces: Flow<Resources<List<Place>>>
    suspend fun getPlace(id: String): Resources<Place>
    suspend fun savePlace(place: Place)
    suspend fun removePlace(id: String)
}

sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)

}