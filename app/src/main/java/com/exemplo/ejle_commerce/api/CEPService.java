package com.exemplo.ejle_commerce.api;

import com.exemplo.ejle_commerce.model.Endereco;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CEPService {

    @GET("{cep}/json/")
    Call<Endereco> recuperarCEP(@Path("cep") String cep);

}
