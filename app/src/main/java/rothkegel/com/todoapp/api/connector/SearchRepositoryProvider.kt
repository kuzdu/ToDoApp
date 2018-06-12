package rothkegel.com.todoapp.api.connector

import io.reactivex.Observable
import retrofit2.Response
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.api.connector.utils.ToDoHolder
import rothkegel.com.todoapp.api.connector.utils.User

object SearchRepositoryProvider {

    private val apiService = ToDoApiService.create()

    fun loginUser(email: String, password: String): Observable<Response<Boolean>> {
        val user = User(email,pwd = password)
        return apiService.putLogin(user)
    }

    fun getToDos(): Observable<Response<Array<ToDo>>> {
        return apiService.getToDos()
    }

}