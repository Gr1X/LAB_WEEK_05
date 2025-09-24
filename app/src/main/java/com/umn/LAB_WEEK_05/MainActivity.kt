package com.umn.LAB_WEEK_05

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_week_05.model.CatBreedData
import com.example.lab_week_05.model.ImageData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    // --- Retrofit and API Service Setup ---
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    // --- View Declarations ---
    private val apiResponseView: TextView by lazy {
        findViewById(R.id.api_response)
    }
    private val imageResultView: ImageView by lazy {
        findViewById(R.id.image_result)
    }
    private val temperamentTextView: TextView by lazy {
        findViewById(R.id.temperament_text)
    }
    private val originTextView: TextView by lazy {
        findViewById(R.id.origin_text)
    }
    private val lifespanTextView: TextView by lazy {
        findViewById(R.id.lifespan_text)
    }
    private val imageLoader: ImageLoader by lazy {
        GlideLoader(this)
    }

    // --- Activity Lifecycle ---
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Memulai alur pengambilan data
        fetchBreedsAndDisplayCat()
    }

    /**
     * Langkah 1: Mengambil daftar semua ras kucing dari API.
     * Jika berhasil, akan memanggil fungsi untuk mencari gambar berdasarkan ras pertama yang valid.
     */
    private fun fetchBreedsAndDisplayCat() {
        catApiService.getBreeds().enqueue(object : Callback<List<CatBreedData>> {
            override fun onFailure(call: Call<List<CatBreedData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get breeds list", t)
                apiResponseView.text = "Error fetching breeds."
            }

            override fun onResponse(call: Call<List<CatBreedData>>, response: Response<List<CatBreedData>>) {
                if (response.isSuccessful) {
                    val breeds = response.body()
                    // Cari ras pertama yang memiliki ID dan nama yang valid
                    val firstValidBreed = breeds?.firstOrNull { !it.id.isNullOrBlank() && !it.name.isNullOrBlank() }

                    if (firstValidBreed != null) {
                        // Jika ras ditemukan, lanjutkan ke Langkah 2
                        searchCatImageByBreed(firstValidBreed.id)
                    } else {
                        Log.d(MAIN_ACTIVITY, "No valid breeds found in the list.")
                        apiResponseView.text = "No valid breeds available."
                    }
                } else {
                    Log.e(MAIN_ACTIVITY, "Failed to get a successful response for breeds.")
                }
            }
        })
    }

    /**
     * Langkah 2: Mengambil gambar kucing spesifik berdasarkan ID ras yang didapat dari Langkah 1.
     * Kemudian menampilkan semua informasi lengkap ke UI.
     */
    private fun searchCatImageByBreed(breedId: String) {
        catApiService.searchImages(1, breedId).enqueue(object : Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get image for breed ID: $breedId", t)
            }

            override fun onResponse(call: Call<List<ImageData>>, response: Response<List<ImageData>>) {
                if (response.isSuccessful) {
                    val images = response.body()
                    val firstImageObject = images?.firstOrNull()
                    val firstBreedData = firstImageObject?.breeds?.firstOrNull()

                    // --- Load Image ---
                    val imageUrl = firstImageObject?.imageUrl.orEmpty()
                    if (imageUrl.isNotBlank()) {
                        imageLoader.loadImage(imageUrl, imageResultView)
                    }

                    // --- Extract and Display All Data ---
                    val catBreed = firstBreedData?.name ?: "No name data"
                    val temperament = firstBreedData?.temperament ?: "No data"
                    val origin = firstBreedData?.origin ?: "No data"
                    val lifeSpan = firstBreedData?.lifeSpan ?: "No data"

                    apiResponseView.text = "Cat Breed: $catBreed"
                    temperamentTextView.text = "Temperament: $temperament"
                    originTextView.text = "Origin: $origin"
                    lifespanTextView.text = "Life Span: $lifeSpan years"
                }
            }
        })
    }

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }
}