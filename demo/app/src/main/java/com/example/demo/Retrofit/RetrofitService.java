package com.example.demo.Retrofit;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {


    public APIUser getApiService() {
        return retrofit.create(APIUser.class);
    }


    private static final String BASE_URL = "http://192.168.70.170:8080";
    private static Retrofit retrofit = null;

    public RetrofitService() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
