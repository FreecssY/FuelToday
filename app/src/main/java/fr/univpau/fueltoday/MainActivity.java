package fr.univpau.fueltoday;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Build the Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://data.economie.gouv.fr/api/explore/v2.1/catalog/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create the API service
        FuelApiService apiService = retrofit.create(FuelApiService.class);

        // Define your query parameters (latitude, longitude, and radius)
        double longitude = -0.360448;
        double latitude = 43.319296;
        int radius = 1; // in kilometers

        String query = "within_distance(geom,GEOM'POINT(" + longitude + " " + latitude + ")'," + radius + "km)";


        // Make the API call
        Call<FuelApiResponse> call = apiService.getFuelPrices(query);
        Log.e("newTag", "Response data00: ");
        call.enqueue(new Callback<FuelApiResponse>() {

            @Override
            public void onResponse(@Nullable Call<FuelApiResponse> call, @Nullable Response<FuelApiResponse> response) {
                Log.e("newTag", "Response data: ");
                if (response != null && response.isSuccessful()) {
                    Log.e("newTag", "Response data11: ");

                    FuelApiResponse fuelApiResponse = response.body();


                    if (fuelApiResponse != null) {
                        List<FuelRecord> fuelRecords = fuelApiResponse.getResults();
                        Log.e("newTag", "FuelRecords data : " + fuelRecords);
                        if (fuelRecords != null) {
                            for (FuelRecord fuelRecord : fuelRecords) {
                                String services = fuelRecord.getServices();
                                String address = fuelRecord.getAddress();

                                // Log the data to check the response
                                Log.d("Services", "Services: " + services);
                                Log.d("Address", "Address: " + address);
                            }

                        } else {
                            Log.e("Response Error", "Fuel records are null");
                        }
                    } else {
                        Log.e("Response Error", "Response body is null");
                    }
                } else {
                    Log.e("Response Error", "Response not successful, HTTP code: " + response.code());
                }

            }


            @Override
            public void onFailure(@Nullable Call<FuelApiResponse> call, @Nullable Throwable t) {
                // Handle network or request error
                Log.e("Network Error", "API request failed", t);
            }

        });

    }
}
class FuelApiResponse {
    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("results")
    private List<FuelRecord> results;

    public int getTotalCount() {
        return totalCount;
    }

    public List<FuelRecord> getResults() {
        return results;
    }
}

class FuelRecord {
    @SerializedName("id")
    private long id;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("cp")
    private String postalCode;

    @SerializedName("pop")
    private String pop;

    @SerializedName("adresse")
    private String address;

    @SerializedName("ville")
    private String city;

    @SerializedName("horaires")
    private String horaires;

    @SerializedName("services")
    private String services;

    @SerializedName("prix")
    private List<FuelPrice> prices;

    @SerializedName("geom")
    private GeoLocation geom;

    // Add getters for the above fields
    public long getId() {
        return id;
    }

    public String getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPop() {
        return pop;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getHoraires() {
        return horaires;
    }

    public String getServices() {
        return services;
    }

    public List<FuelPrice> getPrices() {
        return prices;
    }

    public GeoLocation getGeom() {
        return geom;
    }


}

class FuelPrice {
    @SerializedName("@nom")
    private String name;

    @SerializedName("@id")
    private int id;

    @SerializedName("@maj")
    private String lastUpdated;

    @SerializedName("@valeur")
    private String value;

    // Add getters for the above fields
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getValue() {
        return value;
    }
}

class GeoLocation {
    @SerializedName("lon")
    private double longitude;

    @SerializedName("lat")
    private double latitude;

    // Add getters for the above fields
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}


interface FuelApiService {
    @GET("datasets/prix-des-carburants-en-france-flux-instantane-v2/records")
    Call<FuelApiResponse> getFuelPrices(
            @Query("where") String query
    );
}



