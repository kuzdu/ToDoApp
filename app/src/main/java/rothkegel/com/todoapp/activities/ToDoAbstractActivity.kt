package rothkegel.com.todoapp.activities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import rothkegel.com.todoapp.api.connector.ToDoServiceClient
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.api.connector.utils.User
import rothkegel.com.todoapp.database.ToDoDBHelper
import rothkegel.com.todoapp.models.DatabaseToDo
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket


const val baseUrl = "http://192.168.178.20"
const val baseUrlIp = "192.168.178.20"
const val port = 8080


open class ToDoAbstractActivity : AppCompatActivity() {


    internal val toDoDetailUpdateRequestCode = 10
    internal val toDoDetailAddRequestCode = 11
    internal val toDoIdentifier = "toDoIdentifier"
    internal val removedToDoIdentifier = "removedToDoIdentifier"
    private val HAS_INTERNET_PREFERENCE = "HAS_INTERNET_PREFERENCE"
    private val HAS_INTERNET_KEY = "HAS_INTERNET_KEY"

    lateinit var toDoDBHelper: ToDoDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toDoDBHelper = ToDoDBHelper(this)
    }

    //"adapters"
    open fun insertToDoSQL(toDo: ToDo): ToDo? {
        val databaseToDo = DatabaseToDo(toDo)
        if (toDoDBHelper.insertToDo(databaseToDo)) {
            return toDo
        }
        return null
    }

    open fun onToDoInsertSQL(toDo: ToDo) {
        if (hasInternet()) {
            addToDo(toDo)
            return
        }
        onToDoAdded(toDo)
    }


    open fun hasInternet(): Boolean {
        val prefs = getSharedPreferences(HAS_INTERNET_PREFERENCE, Context.MODE_PRIVATE)
        val restoredInternetStatus = prefs.getBoolean(HAS_INTERNET_KEY, true)
        return restoredInternetStatus
    }

    open fun saveInternetStatus(hasInternet: Boolean) {
        val editor = getSharedPreferences(HAS_INTERNET_PREFERENCE, Context.MODE_PRIVATE).edit()
        editor.putBoolean(HAS_INTERNET_KEY, hasInternet)
        editor.apply()
    }

    open fun fetchToDos() {
        if (this.hasInternet()) {
            fetchToDosFromApi()
        } else {
            fetchToDosSQL()
        }
    }

    open fun updateToDoSQL(toDo: ToDo) {
        val databaseToDo = DatabaseToDo(toDo)
        val successful = toDoDBHelper.changeToDo(databaseToDo)
        if (successful) {
            if (hasInternet()) {
                updateToDo(toDo)
                return
            }
            onToDoUpdated(toDo)
        }
    }

    open fun removeToDoSQL(toDoId: Int) {
        val successful = toDoDBHelper.deleteById(toDoId.toLong())
        if (successful) {
            if (hasInternet()) {
                removeToDo(toDoId)
                return
            }
            onToDoRemoved(true)
        }
    }


    open fun getToDosSQL(): Array<ToDo> {
        val databaseToDos = toDoDBHelper.readAllTodos()

        val toDos: MutableList<ToDo> = arrayListOf()

        for (databaseToDo in databaseToDos) {
            val toDo = ToDo()
            toDo.id = databaseToDo.id.toInt()
            toDo.name = databaseToDo.name
            toDo.description = databaseToDo.description
            toDo.favourite = databaseToDo.favourite
            toDo.done = databaseToDo.done
            toDo.expiry = databaseToDo.expiry


            val contacts = databaseToDo.contacts
            if (!contacts.isNullOrEmpty()) {
                toDo.contacts = databaseToDo.contacts.split("|").toTypedArray()
            }
            toDos.add(toDo)
        }
        return toDos.toTypedArray()
    }

    private fun fetchToDosSQL() {

        val databaseToDos = toDoDBHelper.readAllTodos()

        val toDos: MutableList<ToDo> = arrayListOf()

        for (databaseToDo in databaseToDos) {
            val toDo = ToDo()
            toDo.id = databaseToDo.id.toInt()
            toDo.name = databaseToDo.name
            toDo.description = databaseToDo.description
            toDo.favourite = databaseToDo.favourite
            toDo.done = databaseToDo.done
            toDo.expiry = databaseToDo.expiry


            val contacts = databaseToDo.contacts
            if (!contacts.isNullOrEmpty()) {
                toDo.contacts = databaseToDo.contacts.split("|").toTypedArray()
            }
            toDos.add(toDo)
        }
        onToDosFetched(toDos.toTypedArray())
    }

    //listener
    open fun onToDoUpdated(toDo: ToDo?) {
        toast("Updated ${toDo?.name}")
    }

    open fun onToDosFetched(toDos: Array<ToDo>?) {
        // toast("Got ${toDos?.size} ToDos")
    }


    open fun clearDataBaseEntries(): Boolean {
        return toDoDBHelper.deleteAllTodos()
    }

    open fun onLoggedInUser(loggedIn: Boolean?) {
        // toast("Logged in $loggedIn")
    }

    fun onSingleTodoFetched(toDo: ToDo?) {
        // toast("ToDo Name ${toDo?.name.toString()}")
    }

    fun onError(error: Throwable) {
        toast(error.localizedMessage)
//        error.printStackTrace()
    }

    open fun onRemoveAllAPIToDos(removed: Boolean?) {
        // toast("Deleted: $removed")
    }

    open fun onToDoRemoved(removed: Boolean?) {
        //toast("Removed: $removed")
    }

    open fun onToDoAdded(toDo: ToDo?) {
        toast("Added ${toDo?.name}")
    }

    //request
    private fun fetchToDosFromApi() {
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

    open fun loginUser(user: User) {
        ToDoServiceClient.loginUser(user).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onLoggedInUser(result.body())
                }, { error ->
                    onError(error)
                })
    }


    fun removeAllAPIToDos() {
        ToDoServiceClient.removeAllToDos().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onRemoveAllAPIToDos(result.body())
                }, { error ->
                    onError(error)
                })

    }

    private fun removeToDo(id: Int) {
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

    private fun updateToDo(toDo: ToDo) {
        ToDoServiceClient.updateToDo(toDo, toDo.id).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onToDoUpdated(result.body())
                }, { error ->
                    onError(error)
                })
    }

    fun hasInternetConnection(): Single<Boolean> {
        return Single.fromCallable {
            try {
                // Connect to Google DNS to check for connection
                val timeoutMs = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress(baseUrlIp, port)

                socket.connect(socketAddress, timeoutMs)
                socket.close()

                true
            } catch (e: IOException) {
                false
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
