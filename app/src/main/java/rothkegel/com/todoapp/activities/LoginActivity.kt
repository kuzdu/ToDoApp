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
        val contacts = arrayOf("Michael", "Jonas", "Lara")

        val location = Location("Home", LatLng(50.837, 8.18))

        val toDo = ToDo(1234, "MyName", "This is a description", 0, false, true, contacts, location)


        addToDo(toDo)
    }


    override fun onToDoAdded(toDo: ToDo?) {
        super.onToDoAdded(toDo)

        val updatedToDo = ToDo()
        val updatedLocation = Location("New Home", LatLng(8.837, 50.18))
        val updatedContacts = arrayOf("Frank", "Andy", "Tim")

        updatedToDo.id = 1234
        updatedToDo.name = "Testname"
        updatedToDo.description = "New Updated Description"
        updatedToDo.done = false
        updatedToDo.expiry = 1
        updatedToDo.favourite = false
        updatedToDo.contacts = updatedContacts
        updatedToDo.location = updatedLocation
        updateToDo(updatedToDo, 1234)
    }

}
