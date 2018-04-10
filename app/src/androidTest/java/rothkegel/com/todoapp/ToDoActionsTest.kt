package rothkegel.com.todoapp

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import rothkegel.com.todoapp.database.ToDoDBHelper
import rothkegel.com.todoapp.models.ToDo

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ToDoActionsTest {
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

        val milkToDo = generateToDo("Milk", "from farmer", "2018-11-31 17:05:43", true, true)
        toDoDBHelper.insertTodo(milkToDo)

        val bananaToDo = generateToDo("Banana", "from store", "2018-12-31 17:05:43", false, false)
        toDoDBHelper.insertTodo(bananaToDo)

        val toDos = toDoDBHelper.readAllTodos()

        assertEquals("Milk", toDos.first().name)
        assertEquals("from farmer", toDos.first().description)
        assertEquals("2018-11-31 17:05:43", toDos.first().dueDate)
        assertEquals(true, toDos.first().favorite)
        assertEquals(true, toDos.first().done)
        assertEquals(2, toDos.count())
    }

    @Test
    fun deleteToDo() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val toDoDBHelper = ToDoDBHelper(appContext)

        val milkToDo = generateToDo("Milk", "from farmer", "2018-11-31 17:05:43", true, true)
        toDoDBHelper.insertTodo(milkToDo)

        assertEquals(1, toDoDBHelper.readAllTodos().count())
        assertEquals(true, toDoDBHelper.deleteById(toDoDBHelper.readAllTodos().first().id))
        assertEquals(0, toDoDBHelper.readAllTodos().count())
    }

    /** date format yyyy-MM-dd HH:mm:ss */
    private fun generateToDo(name: String, description: String, dueDate: String, favorite: Boolean, done: Boolean): ToDo {
        val toDo = ToDo()
        toDo.name = name
        toDo.description = description
        toDo.dueDate = dueDate
        toDo.favorite = favorite
        toDo.done = done
        return toDo
    }
}
