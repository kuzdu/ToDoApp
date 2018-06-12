package rothkegel.com.todoapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.SearchRepositoryProvider
import rothkegel.com.todoapp.api.connector.utils.ToDo

class LoginActivity : ToDoAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        getLogin()
        fetchTodos()

    }

    private fun onToDosFetched(todos: Array<ToDo>?) {
        toast("Got ${todos?.size} ToDos")
    }

    private fun onLoggedIn(loggedIn: Boolean?) {
        toast("Logged in $loggedIn")
    }

    private fun onError(error: Throwable) {
        toast(error.localizedMessage)
//        error.printStackTrace()
    }

    private fun fetchTodos() {
        SearchRepositoryProvider.getToDos().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onToDosFetched(result.body())
                }, { error ->
                    onError(error)
                })
    }

    private fun getLogin() {
        SearchRepositoryProvider.loginUser("s@bht.de", "000000").observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onLoggedIn(result.body())
                }, { error ->
                    onError(error)
                })

    }
}
