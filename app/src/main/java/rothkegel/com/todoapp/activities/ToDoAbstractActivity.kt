package rothkegel.com.todoapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import rothkegel.com.todoapp.api.connector.ToDoServiceClient
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.database.ToDoDBHelper

open class ToDoAbstractActivity : AppCompatActivity() {

    lateinit var toDoDBHelper: ToDoDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toDoDBHelper = ToDoDBHelper(this)


        /*
        val todo = ToDoOld()
        todo.name = "Name"
        todo.description = "This is a description"
        todo.dueDate = "Date"
        todo.favorite = true
        todo.done = true
        toDoDBHelper.insertTodo(todo)


        val todos = toDoDBHelper.readAllTodos()
        print(todos.first().toString())

        for (todo in todos) {
            Log.d("OUTPUT", todo.toString())
        }*/

    }

    //listener
    open fun onToDoUpdated(toDo: ToDo?) {
        toast("Updated ${toDo.toString()}")
    }

    open fun onToDosFetched(todos: Array<ToDo>?) {
        toast("Got ${todos?.size} ToDos")
    }

    fun onLoggedIn(loggedIn: Boolean?) {
        toast("Logged in $loggedIn")
    }

    fun onSingleTodoFetched(toDo: ToDo?) {
        toast("ToDo Name ${toDo?.name.toString()}")
    }

    fun onError(error: Throwable) {
        toast(error.localizedMessage)
//        error.printStackTrace()
    }

    fun onDeletedAllToDos(removed: Boolean?) {
        toast("Deleted: $removed")
    }

    fun onToDoRemoved(removed: Boolean?) {
        toast("Removed: $removed")
    }

    open fun onToDoAdded(toDo: ToDo?) {
        toast("Added ${toDo?.name}")
    }


    //request
    fun fetchToDos() {
        ToDoServiceClient.fetchToDos().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onToDosFetched(result.body())
                }, { error ->
                    onError(error)
                })
    }

    fun fetchToDo(id: Int) {
        ToDoServiceClient.fetchToDo(id).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onSingleTodoFetched(result.body())
                }, { error ->
                    onError(error)
                })
    }

    fun getLogin() {
        ToDoServiceClient.loginUser("s@bht.de", "000000").observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onLoggedIn(result.body())
                }, { error ->
                    onError(error)
                })
    }

    fun deleteAllToDos() {
        ToDoServiceClient.removeAllToDos().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onDeletedAllToDos(result.body())
                }, { error ->
                    onError(error)
                })

    }

    fun removeToDo(id: Int) {
        ToDoServiceClient.removeToDo(id).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onToDoRemoved(result.body())
                }, { error ->
                    onError(error)
                })
    }

    fun addToDo(toDo: ToDo) {
        ToDoServiceClient.addToDo(toDo).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onToDoAdded(result.body())
                }, { error ->
                    onError(error)
                })
    }

    fun updateToDo(toDo: ToDo, id: Int) {
        ToDoServiceClient.updateToDo(toDo, id).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onToDoUpdated(result.body())
                }, { error ->
                    onError(error)
                })
    }
}
