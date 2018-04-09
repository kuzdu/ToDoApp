package rothkegel.com.todoapp.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.database.ToDoDBHelper
import rothkegel.com.todoapp.models.ToDo

class StartActivity : AppCompatActivity() {

    lateinit var toDoDBHelper: ToDoDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        toDoDBHelper = ToDoDBHelper(this)



        val todo = ToDo()
        todo.id = "ID1"
        todo.name = "Name"
        todo.description = "This is a description"
        todo.dueDate = "Date"
        todo.favorite = true
        todo.done = true

        toDoDBHelper.insertTodo(todo)


        val todos = toDoDBHelper.readAllTodos()


        print(todos.first().name)

    }
}
