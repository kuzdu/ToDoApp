package rothkegel.com.todoapp.activities

import android.os.Bundle
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.LatLng
import rothkegel.com.todoapp.api.connector.utils.Location
import rothkegel.com.todoapp.api.connector.utils.ToDo


/*
    Next Steps:
    -Datum Operationen
    -API + Persitente Datenbank verheiraten
    -Andere Anforderungen durchlesen, die nicht UI sind
 */

class LoginActivity : ToDoAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        getLogin()
        fetchToDos()
        fetchToDo(1)
//        fetchToDo(100)
//        deleteAllToDos()
        removeToDo(1)
        fetchToDo(0)


        //ggf. das ausprobieren https://discuss.kotlinlang.org/t/how-to-append-a-list-to-an-immutable-list-of-lists/38
        val contacts = arrayOf("Michael","Jonas","Lara")

        val location = Location("Home", LatLng(50.837, 8.18))

        //val toDo = ToDo(1234, "MyName", "This is a description", 0, false, true, contacts, location)
        val toDo = ToDo()
        toDo.id = 1234123
        toDo.name = "Test"
        toDo.description = "This is a descirption"
        toDo.done = true
        toDo.expiry = 0
        toDo.favourite = true
        toDo.contacts = contacts
        toDo.location = location
        addToDo(toDo)

    }


}
