package rothkegel.com.todoapp

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import rothkegel.com.todoapp.database.ToDoDBHelper
import rothkegel.com.todoapp.models.ToDoOld


@RunWith(AndroidJUnit4::class)
class ToDoOldActionsTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("rothkegel.com.todoapp", appContext.packageName)
    }

    @Test
    fun addToDos() {

        val appContext = InstrumentationRegistry.getTargetContext()
        val toDoDBHelper = ToDoDBHelper(appContext)
        val allToDosBefore = toDoDBHelper.readAllTodos().count()


        val milkToDo = generateToDo("Milk", "from farmer", "2018-11-31 17:05:43", true, true)
        val milkToDoId = toDoDBHelper.insertTodo(milkToDo)

        val bananaToDo = generateToDo("Banana", "from store", "2018-12-31 17:05:43", false, false)
        toDoDBHelper.insertTodo(bananaToDo)

        val toDos = toDoDBHelper.readAllTodos()

        val milkToDoFromDatabase = toDos.find { e -> e.id == milkToDoId }

        assertEquals("Milk", milkToDoFromDatabase?.name)
        assertEquals("from farmer", milkToDoFromDatabase?.description)
        assertEquals("2018-11-31 17:05:43", milkToDoFromDatabase?.dueDate)
        assertEquals(true, milkToDoFromDatabase?.favorite)
        assertEquals(true, milkToDoFromDatabase?.done)
        assertEquals(allToDosBefore+2, toDos.count())
    }

    @Test
    fun changeToDo() {

        val appContext = InstrumentationRegistry.getTargetContext()
        val toDoDBHelper = ToDoDBHelper(appContext)

        val milkToDo = generateToDo("Milk", "from farmer", "2018-11-31 17:05:43", true, true)
        val id = toDoDBHelper.insertTodo(milkToDo)

        val milkToDoFromDataBase = toDoDBHelper.readToDoById(id)

        assertNotNull(milkToDoFromDataBase)
        assertEquals("Milk", milkToDoFromDataBase?.name)
        assertEquals("from farmer", milkToDoFromDataBase?.description)
        assertEquals("2018-11-31 17:05:43", milkToDoFromDataBase?.dueDate)
        assertEquals(true, milkToDoFromDataBase?.favorite)
        assertEquals(true, milkToDoFromDataBase?.done)

        val changeToDo = ToDoOld()
        changeToDo.id = milkToDoFromDataBase!!.id
        changeToDo.name = "Banana"
        changeToDo.description = "From Store"
        changeToDo.done = false
        changeToDo.favorite = false
        changeToDo.dueDate = ""

        assertEquals(true, toDoDBHelper.changeToDo(changeToDo))

        val changedToDoFromDataBase = toDoDBHelper.readToDoById(id)

        assertNotNull(changedToDoFromDataBase)
        assertEquals("Banana", changedToDoFromDataBase?.name)
        assertEquals("From Store", changedToDoFromDataBase?.description)
        assertEquals("", changedToDoFromDataBase?.dueDate)
        assertEquals(false, changedToDoFromDataBase?.favorite)
        assertEquals(false, changedToDoFromDataBase?.done)
    }

    @Test
    fun deleteToDo() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val toDoDBHelper = ToDoDBHelper(appContext)

        val allToDosBefore = toDoDBHelper.readAllTodos().count()
        val milkToDo = generateToDo("Milk", "from farmer", "2018-11-31 17:05:43", true, true)
        val id = toDoDBHelper.insertTodo(milkToDo)

        assertEquals(allToDosBefore+1, toDoDBHelper.readAllTodos().count())
        assertEquals(true, toDoDBHelper.deleteById(id))
        assertEquals(allToDosBefore, toDoDBHelper.readAllTodos().count())
    }

    /** date format yyyy-MM-dd HH:mm:ss */
    private fun generateToDo(name: String, description: String, dueDate: String, favorite: Boolean, done: Boolean): ToDoOld {
        val toDo = ToDoOld()
        toDo.name = name
        toDo.description = description
        toDo.dueDate = dueDate
        toDo.favorite = favorite
        toDo.done = done
        return toDo
    }
}
