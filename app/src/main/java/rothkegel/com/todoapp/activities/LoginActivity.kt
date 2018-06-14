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

        val contacts = arrayOf("Michael", "Jonas", "Lara")
        val location = Location("Home", LatLng(50.837, 8.18))
        val toDo1 = ToDo(1, "A", "1", 999, false, true, contacts, location)
        val toDo2 = ToDo(2, "B", "2", 5, true, false, contacts, location)
        val toDo3 = ToDo(3, "C", "3", 10000, true, true, contacts, location)
        val toDo4 = ToDo(4, "D", "4", 1000, false, false, contacts, location)
        val toDo5 = ToDo(5, "E", "5", 8000, false, false, contacts, location)

/*
    erwartet Reihenfolge
    3,2,1,5,4


 */


        var toDos = arrayOf(toDo1, toDo2, toDo3, toDo4, toDo5)

        val allSorted = SortTool.sortByDone(toDos, false, false)

        toast("Nice")
    }

    override fun onToDosFetched(toDos: Array<ToDo>?) {
        super.onToDosFetched(toDos)

        if (toDos == null) {
            toast("Todos is null")
            return
        }

        val allSorted = SortTool.sortByDone(toDos, false, false)


        toast("Sorted")
    }

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
