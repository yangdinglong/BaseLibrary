package com.roobo.baselibiray.utils.net;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by user on 2017/8/29.
 */
public interface ApiService {

    @POST
    Call<JsonObject> post(@Url String url);

    @POST
    Call<JsonObject> post(@Url String url, @Body RequestBody body);

    @GET
    Call<JsonObject> get(@Url String url);

    @POST
    @Multipart
    Call<JsonObject> upload(@Url String url, @Part("fileName") String fileName, @Part("file\"; filename=\"registerVoice.wav") RequestBody file);

    @PATCH
    Call<JsonObject> patch(@Url String url, @Body RequestBody body);

}
