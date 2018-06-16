package rothkegel.com.todoapp.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.todo_list_activity.*
import org.jetbrains.anko.toast
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.tools.SortTool

class ToDoListActivity : ToDoAbstractActivity(), ClickListener {

    private var toDos: ArrayList<ToDo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_list_activity)
        fetchToDos()
    }

    //interface implements
    override fun onDoneClicked(toDo: ToDo) {
        updateToDo(toDo)
    }

    override fun onFavouriteClicked(toDo: ToDo) {
        updateToDo(toDo)
    }

    //api - on response
    override fun onToDoUpdated(toDo: ToDo?) {
        super.onToDoUpdated(toDo)

        if (toDo == null) {
            return
        }

        updateLocalToDosWith(toDo)
        this.toDos = SortTool.getSortedByDoneFalseThenExpiryThenFavourite(toDos)
        setAdapter(toDos)
        updateUI()
        toast(getString(R.string.to_do_list_successful_update_message))
    }

    override fun onToDosFetched(toDos: Array<ToDo>?) {
        super.onToDosFetched(toDos)

        if (toDos == null) {
            return
        }

        this.toDos = ArrayList(toDos.toList())
        todo_list_items.layoutManager = LinearLayoutManager(this)
        setAdapter(toDos.toList())
    }


    private fun setAdapter(toDos: List<ToDo>) {
        val adapter = ToDoListAdapter(ArrayList(toDos.toList()), this)
        todo_list_items.adapter = adapter
        adapter?.setClickListenerCallback(this)
    }

    private fun updateLocalToDosWith(toDo: ToDo) {
        val foundToDo = toDos.find { t -> t.id == toDo.id }
        if (foundToDo == null) {
            toast("Updated Todo konnte nicht in der lokalen Liste gefunden werden.")
            return
        }
        val index = toDos.indexOf(foundToDo)
        this.toDos[index] = toDo
    }

    private fun updateUI() {
        todo_list_items.adapter.notifyDataSetChanged()
    }
}