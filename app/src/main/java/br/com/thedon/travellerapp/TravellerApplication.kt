package br.com.thedon.travellerapp

import android.app.Application
import br.com.thedon.travellerapp.data.AppContainer
import br.com.thedon.travellerapp.data.AppDataContainer
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.intercept.Interceptor
import coil.request.ImageResult
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class TravellerApplication: Application(), ImageLoaderFactory {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }

    /**
     * Create the singleton [ImageLoader].
     * This is used by [RemoteImage] to load images in the app.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(FirebaseStorageInterceptor())
            }
            // Ignore the Unsplash cache headers as they set `Cache-Control:must-revalidate` which
            // requires a network operation even if the image is cached locally.
            .respectCacheHeaders(false)
            .build()
    }
}

class FirebaseStorageInterceptor : Interceptor {
    private val storage = FirebaseStorage.getInstance()

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val data = chain.request.data
        if (data is String && data.startsWith("gs://")) {
            // Replace the gs:// URI with the actual image URL
            val imageUrl = storage.getReferenceFromUrl(data).downloadUrl.await()
            val newRequest = chain.request.newBuilder().data(imageUrl).build()
            return chain.proceed(newRequest)
        }
        return chain.proceed(chain.request)
    }
}
