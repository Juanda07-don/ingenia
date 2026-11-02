package com.example.myapplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Si pruebas con el emulador de Android Studio, se usa 10.0.2.2 (apunta a tu PC local)
    // Si pruebas en un celular físico por Wi-Fi, aquí se pondría la IP de tu PC (ej: 192.168.1.15)
    private static final String BASE_URL = "http://localhost:8080/";
    private static Retrofit retrofit = null;

    public static ApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
