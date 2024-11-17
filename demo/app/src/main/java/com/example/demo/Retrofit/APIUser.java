package com.example.demo.Retrofit;


import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.Model.Music;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import java.time.LocalDateTime;
import java.util.Map;

public interface APIUser {
    @GET("/")
    Call<List<Music>> getAll();




}


