package rothkegel.com.todoapp.api.connector

import io.reactivex.Observable
import retrofit2.Response
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.api.connector.utils.User

object ToDoServiceClient {

    private val apiService = ToDoApiService.create()

    fun loginUser(email: String, password: String): Observable<Response<Boolean>> {
        val user = User(email, pwd = password)
        return apiService.putLogin(user)
    }

    fun fetchToDo(id: Int): Observable<Response<ToDo>> {
        return apiService.fetchToDo(id)
    }

    fun fetchToDos(): Observable<Response<Array<ToDo>>> {
        return apiService.fetchToDos()
    }

    fun removeAllToDos(): Observable<Response<Boolean>> {
        return apiService.removeAllToDos()
    }

    fun removeToDo(id: Int): Observable<Response<Boolean>> {
        return apiService.removeToDo(id)
    }
}