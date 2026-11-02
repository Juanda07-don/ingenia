package com.example.myapplication;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/estudiantes/{id}/asesorias")
    Call<List<AsesoriaDTO>> getAsesoriasEstudiante(@Path("id") Long idEstudiante);

    @GET("api/profesores/{id}/asesorias")
    Call<List<AsesoriaDTO>> getAsesoriasProfesor(@Path("id") Long idProfesor);

    @GET("api/profesores")
    Call<List<ProfesorDTO>> buscarPorMateria(@Query("materia") String materia);

    @GET("api/profesores")
    Call<List<ProfesorDTO>> buscarPorNombre(@Query("nombre") String nombre);

    @GET("api/profesores/{id}/disponibilidad")
    Call<List<DisponibilidadDTO>> getDisponibilidad(@Path("id") Long idProfesor, @Query("fecha") String fecha);

    @POST("api/asesorias")
    Call<AsesoriaDTO> reservarAsesoria(@Body ReservaRequest request);

    @POST("api/asesorias/{id}/atencion")
    Call<ReporteDTO> registrarAtencion(@Path("id") Long idAsesoria, @Body RegistroAtencionRequest request);

    @GET("api/asesorias/{id}/reporte")
    Call<ReporteDTO> getReporte(@Path("id") Long idAsesoria, @Query("usuarioId") Long usuarioId);

    @POST("api/asesorias/{id}/reprogramacion")
    Call<AsesoriaDTO> reprogramarAsesoria(@Path("id") Long idAsesoria, @Body ReprogramarRequest request);

    @POST("api/asesorias/{id}/respuesta-reprogramacion")
    Call<AsesoriaDTO> responderReprogramacion(@Path("id") Long idAsesoria, @Body RespuestaReprogramacionRequest request);

    @GET("api/profesores/{id}/materias")
    Call<List<MateriaDTO>> getMateriasProfesor(@Path("id") Long idProfesor);

    @POST("api/profesores/{profesorId}/espacios")
    Call<List<EspacioDTO>> crearEspacios(@Path("profesorId") Long profesorId, @Body ConfigurarEspaciosRequest request);
}