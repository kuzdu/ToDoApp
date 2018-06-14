package rothkegel.com.todoapp.api.connector

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.api.connector.utils.User

interface ToDoApiService {

    @Headers("Content-Type: application/json")
    @PUT("todos/{id}")
    fun putToDo(@Body toDo: ToDo,@Path("id") id: Int): Observable<Response<ToDo>>

    @Headers("Content-Type: application/json")
    @PUT("users/auth")
    fun putLogin(@Body user: User): Observable<Response<Boolean>>

    @GET("todos/{id}")
    fun fetchToDo(@Path("id") id: Int): Observable<Response<ToDo>>

    @GET("todos")
    fun fetchToDos(): Observable<Response<Array<ToDo>>>

    @DELETE("todos")
    fun removeAllToDos(): Observable<Response<Boolean>>

    @DELETE("todos/{id}")
    fun removeToDo(@Path("id") id: Int): Observable<Response<Boolean>>

    @Headers("Content-Type: application/json")
    @POST("todos")
    fun addToDo(@Body toDo: ToDo): Observable<Response<ToDo>>



    /**
     * Companion object to create the ToDoApiService
     */
    companion object Factory {
        fun create(): ToDoApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://192.168.178.20:8080/api/")
                    .build()

            return retrofit.create(ToDoApiService::class.java)
        }
    }
}