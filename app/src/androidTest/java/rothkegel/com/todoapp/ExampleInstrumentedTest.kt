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
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("rothkegel.com.todoapp", appContext.packageName)
    }

    @Test
    fun addToDo() {

        val appContext = InstrumentationRegistry.getTargetContext()

        val toDoDBHelper = ToDoDBHelper(appContext)

        val todo = ToDo()
        todo.name = "Name"
        todo.description = "This is a description"
        todo.dueDate = "Date"
        todo.favorite = true
        todo.done = true

        toDoDBHelper.insertTodo(todo)


        val todos = toDoDBHelper.readAllTodos()


        assertEquals("Name",todos.first().name)

    }

}
