package com.bhsoftworks.liststudentsapp.service;

import com.bhsoftworks.liststudentsapp.model.Student;
import com.bhsoftworks.liststudentsapp.model.StudentReturn;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by leandrodlm on 27/07/17.
 */

public interface StudentAPI {

    String BASE_URL  = "https://parseapi.back4app.com/classes/";

    @Headers({
            "X-Parse-Application-Id: FWmmldOSRF8GE7jR8424Ex9Tu2ZHLTrggQHLJvjY",
            "X-Parse-REST-API-Key: RegHHKDEd3qf260q0mGUM7Z7GMsWry79eKsv3Jic"
    })
    @GET("Aluno")
    Call<StudentReturn> listAlunos();

    @Headers({
            "X-Parse-Application-Id: FWmmldOSRF8GE7jR8424Ex9Tu2ZHLTrggQHLJvjY",
            "X-Parse-REST-API-Key: RegHHKDEd3qf260q0mGUM7Z7GMsWry79eKsv3Jic",
            "Content-type: application/json"
    })
    @POST("Aluno")
    Call<StudentReturn> addAluno(@Body Student student);

    @Headers({
            "X-Parse-Application-Id: FWmmldOSRF8GE7jR8424Ex9Tu2ZHLTrggQHLJvjY",
            "X-Parse-REST-API-Key: RegHHKDEd3qf260q0mGUM7Z7GMsWry79eKsv3Jic"
    })
    @DELETE("Aluno/{objectId}")
    Call<StudentReturn> deleteAluno(@Path("objectId") String alunoId);
}
