package rothkegel.com.todoapp.activities

import android.os.Bundle
import rothkegel.com.todoapp.R

class LoginActivity : ToDoAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        getLogin()
        fetchToDos()
        fetchToDo(0)
        fetchToDo(100)
        deleteAllToDos()


    }


}
