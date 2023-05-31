package com.mmushtaq.bank.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    private static Retrofit retrofit = null;
    private static final Gson gson = new GsonBuilder().create();
    private static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(200, TimeUnit.SECONDS).readTimeout(200, TimeUnit.SECONDS).addInterceptor(interceptor).build();


    public static <T> T createService(Class<T> serviceClass) {


        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://harvester-mis.herokuapp.com/api/v1/")
//                    .baseUrl("http://127.0.0.1:3001/api/v1")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(serviceClass);
    }
}
