package rothkegel.com.todoapp.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import rothkegel.com.todoapp.R
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
}
