import com.example.lab_week_05.model.CatBreedData // Pastikan import ini ada
import com.example.lab_week_05.model.ImageData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApiService {
    @GET("images/search")
    fun searchImages(
        @Query("limit") limit: Int,
        @Query("breed_ids") breedIds: String
    ): Call<List<ImageData>>

    @GET("breeds")
    fun getBreeds(): Call<List<CatBreedData>>
}