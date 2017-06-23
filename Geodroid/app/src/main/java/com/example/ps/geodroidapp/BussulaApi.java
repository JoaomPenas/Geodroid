package com.example.ps.geodroidapp;

import com.example.ps.geodroidapp.Domain.DtoCatalog;
import com.example.ps.geodroidapp.Domain.DtoDiscontinuity;
import com.example.ps.geodroidapp.Domain.User;
import com.example.ps.geodroidapp.Utils.AuthenticateResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface BussulaApi {

    //String BASE_URL ="https://sgeotest.herokuapp.com/";
    String BASE_URL ="http://10.0.2.2:3010";      // para usar com o emulador
    //String BASE_URL ="http://10.10.31.140:3010";
    //String BASE_URL ="http://192.168.1.111:3010"; //para usar com o dispositivo móvel


    @GET("api/users")
        //Call<DtoCatalog> getUserToken();
    Call<ResponseBody> getUser(@Header("x-access-token") String token);


    @POST("api/authenticate")
    Call<AuthenticateResponse>postAuthenticate(@Body User user);
    //@GET("api/sessions")
    //Call<DtoCatalog> getSessions();


    @POST("api/discontinuities")
    //Call <AuthenticateResponse>postDiscontinuities(@Header("x-access-token") String token,@Body DtoDiscontinuity dtoDiscontinuity);
    Call <ResponseBody>postDiscontinuities(@Header("x-access-token") String token,@Body DtoDiscontinuity dtoDiscontinuity);
    //Call <DtoDiscontinuity>postDiscontinuities(@Body DtoDiscontinuity dtoDiscontinuity);

    class Factory{

        private static BussulaApi service;

        public static BussulaApi getInstance(){

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            // add your other interceptors …

            // add logging as last interceptor
            httpClient.addInterceptor(logging);  // <-- this is the important line!

            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build();
                service = retrofit.create(BussulaApi.class);
                return service;
            }
            else {
                return service;
            }
        }
    }
}
