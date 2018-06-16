package rothkegel.com.todoapp.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.todo_list_activity.*
import org.jetbrains.anko.toast
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.ToDo

class ToDoListActivity : ToDoAbstractActivity() {


    private var toDos: List<ToDo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_list_activity)
        fetchToDos()
    }

    override fun onToDosFetched(toDos: Array<ToDo>?) {
        super.onToDosFetched(toDos)

        if (toDos == null) {
            toast("Keine ToDos gefunden")
            return
        }

        this.toDos = toDos.toList()
        todo_list_items.layoutManager = LinearLayoutManager(this)
        todo_list_items.adapter = ToDoListAdapter(this.toDos, this)
    }
}