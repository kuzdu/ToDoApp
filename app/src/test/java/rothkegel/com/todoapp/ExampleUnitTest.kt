package rothkegel.com.todoapp

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import rothkegel.com.todoapp.api.connector.utils.LatLng
import rothkegel.com.todoapp.api.connector.utils.Location
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.tools.SortTool

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    fun getUnSortedToDos(): Array<ToDo> {
        val contacts = arrayOf("Michael", "Jonas", "Lara")
        val location = Location("Home", LatLng(50.837, 8.18))
        val toDo1 = ToDo(1, "A", "1", 999, false, true, contacts, location)
        val toDo2 = ToDo(2, "B", "2", 5, true, false, contacts, location)
        val toDo3 = ToDo(3, "C", "3", 10000, true, true, contacts, location)
        val toDo4 = ToDo(4, "D", "4", 1000, false, false, contacts, location)
        val toDo5 = ToDo(5, "E", "5", 8000, false, false, contacts, location)
        return arrayOf(toDo1, toDo2, toDo3, toDo4, toDo5)
    }


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun sortByDoneFalseThenExpiryThenFavourite() {
        val toDosSorted = SortTool.getSortedByDoneFalseThenExpiryThenFavourite(getUnSortedToDos())  //expected 5,4,1,3,2
        assertEquals(toDosSorted[0].id,5 )
        assertEquals(toDosSorted[1].id,4 )
        assertEquals(toDosSorted[2].id,1 )
        assertEquals(toDosSorted[3].id,3 )
        assertEquals(toDosSorted[4].id,2 )
    }


    @Test
    fun sortByDoneFalseThenFavouriteThenExpiry() {
        val toDosSorted = SortTool.getSortedByDoneFalseThenFavouriteThenExpiry(getUnSortedToDos())   //expeceted  1,5,4,3,2
        assertEquals(toDosSorted[0].id,1 )
        assertEquals(toDosSorted[1].id,5 )
        assertEquals(toDosSorted[2].id,4 )
        assertEquals(toDosSorted[3].id,3 )
        assertEquals(toDosSorted[4].id,2 )
    }

    @Test
    fun sortByDoneThenExpiryThenFavourite() {
        val toDosSorted = SortTool.getSortedByDoneThenExpiryThenFavourite(getUnSortedToDos()) //expected: 3,2,5,4,1
        assertEquals(toDosSorted[0].id,3 )
        assertEquals(toDosSorted[1].id,2 )
        assertEquals(toDosSorted[2].id,5 )
        assertEquals(toDosSorted[3].id,4 )
        assertEquals(toDosSorted[4].id,1 )
    }

    @Test
    fun sortByDoneThenFavouriteThenExpiry() {
        val toDosSorted = SortTool.getSortedByDoneThenFavouriteThenExpiry(getUnSortedToDos()) //expected: 3,2,1,5,4
        assertEquals(toDosSorted[0].id,3 )
        assertEquals(toDosSorted[1].id,2 )
        assertEquals(toDosSorted[2].id,1 )
        assertEquals(toDosSorted[3].id,5 )
        assertEquals(toDosSorted[4].id,4 )
    }

}
