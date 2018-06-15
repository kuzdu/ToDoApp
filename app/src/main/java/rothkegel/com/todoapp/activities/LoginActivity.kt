package rothkegel.com.todoapp.activities

import android.os.Bundle
import org.jetbrains.anko.toast
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.LatLng
import rothkegel.com.todoapp.api.connector.utils.Location
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.tools.DateTool
import rothkegel.com.todoapp.tools.SortTool


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


        //    toast(DateTool.getDateTime(1372339860))


        /*  getLogin()
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


          addToDo(toDo)*/


//        fetchToDos()

//06/27/2013 @ 1:31pm
       // DateTool.getDateTime(1372339860)


        //format dd/MM/yyyy HH:mm:ss
        val timestamp = DateTool.convertUnixtimeToDate("27/06/2013 13:31:00")

        val timeString = DateTool.getDateTime(timestamp)
        toast(timeString)
    }

    /*

    override fun onToDosFetched(toDos: Array<ToDo>?) {
        super.onToDosFetched(toDos)

    }*/

    /* override fun onToDoAdded(toDo: ToDo?) {
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
     }*/

}
