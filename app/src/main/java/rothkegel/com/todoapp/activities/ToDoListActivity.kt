package rothkegel.com.todoapp.activities

import android.os.Bundle
import rothkegel.com.todoapp.R

class ToDoListActivity : ToDoAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_list_activity)


    }
}