package com.mmushtaq.bank.service;

import com.mmushtaq.bank.model.CaseModel;
import com.mmushtaq.bank.model.LoginModel;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserService {

    @FormUrlEncoded
    @POST("auth/sign_in")
    Call<LoginModel> login(@Field("email") String username, @Field("password") String password, @Field("device_type") String device_type);

    @FormUrlEncoded
    @PUT("profile")
    Call<LoginModel> reset(@Header("access-token") String access_token,@Header("client") String client,@Header("uid") String uid,
                           @FieldMap HashMap<String,String> map);

    @DELETE("auth/sign_out")
    Call<CaseModel> sign_out(@Header("access-token") String access_token,@Header("client") String client,@Header("uid") String uid);

    @GET("cases")
    Call<CaseModel> getCases(@Header("access-token") String access_token,@Header("client") String client,@Header("uid") String uid,@Header("Accept") String accept);

    @FormUrlEncoded
    @PUT("cases")
    Call<CaseModel> updateCases(@Header("access-token") String access_token, @Header("client") String client, @Header("uid") String uid
            , @Header("Accept") String accept, @FieldMap ConcurrentHashMap<String,String> map);

}