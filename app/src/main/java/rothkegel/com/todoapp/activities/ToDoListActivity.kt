package rothkegel.com.todoapp.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.todo_list_activity.*
import org.jetbrains.anko.toast
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.tools.SortTool
import com.google.gson.Gson


class ToDoListActivity : ToDoAbstractActivity(), ClickListener {

    private var toDos: ArrayList<ToDo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_list_activity)
        fetchToDos()
        setAddToDoClickListener()
    }

    private fun setAddToDoClickListener() {
        addToDoActionButton.setOnClickListener {
            goToToDoDetail()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.sort_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.menu_done_date_favourite -> {
                this.toDos = SortTool.getSortedByDoneThenExpiryThenFavourite(this.toDos)
                setAdapter(toDos.toList())
                return true
            }
            R.id.menu_done_favourite_date -> {
                this.toDos = SortTool.getSortedByDoneThenFavouriteThenExpiry(this.toDos)
                setAdapter(toDos.toList())
                return true
            }
            R.id.menu_non_done_date_favourite -> {
                this.toDos = SortTool.getSortedByNonDoneThenExpiryThenFavourite(this.toDos)
                setAdapter(toDos.toList())
                return true
            }
            R.id.menu_non_done_favourite_date -> {
                this.toDos = SortTool.getSortedByNonDoneThenFavouriteThenExpiry(this.toDos)
                setAdapter(toDos.toList())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //interface implements
    override fun onDoneClicked(toDo: ToDo) {
        updateToDo(toDo)
    }

    override fun onFavouriteClicked(toDo: ToDo) {
        updateToDo(toDo)
    }

    override fun onToDoItemClicked(position: Int) {
        goToToDoDetail(position)
    }

    private fun goToToDoDetail() {
        val i = Intent(this, ToDoDetailActivity::class.java)
        startActivityForResult(i, toDoDetailUpdateRequestCode)
    }

    private fun goToToDoDetail(position: Int) {
        val i = Intent(this, ToDoDetailActivity::class.java)
        val toDo = toDos[position]
        val gson = Gson()
        i.putExtra(toDoIdentifier, gson.toJson(toDo))
        startActivityForResult(i, toDoDetailUpdateRequestCode)
    }

    //api - on response
    override fun onToDoUpdated(toDo: ToDo?) {
        super.onToDoUpdated(toDo)
        if (toDo == null) return
        updateLocalToDosWith(toDo)
        toast(getString(R.string.to_do_list_successful_update_message))
    }

    override fun onToDosFetched(toDos: Array<ToDo>?) {
        super.onToDosFetched(toDos)
        if (toDos == null) return
        this.toDos = ArrayList(toDos.toList())
        this.toDos = SortTool.getSortedByNonDoneThenFavouriteThenExpiry(this.toDos)
        todo_list_items.layoutManager = LinearLayoutManager(this)
        setAdapter(this.toDos.toList())
    }


    private fun setAdapter(toDos: List<ToDo>) {
        val adapter = ToDoListAdapter(ArrayList(toDos.toList()), this)
        todo_list_items.adapter = adapter
        adapter.setClickListenerCallback(this)
        todo_list_items.adapter.notifyDataSetChanged()
    }

    private fun updateLocalToDosWith(toDo: ToDo) {
        val foundToDo = toDos.find { t -> t.id == toDo.id } ?: return

        val index = toDos.indexOf(foundToDo)
        this.toDos[index] = toDo

        setAdapter(this.toDos.toList())
    }

    private fun removeLocalToDoWith(id: Int) {
        val foundToDo = toDos.find { t -> t.id == id } ?: return

        val index = toDos.indexOf(foundToDo)
        toDos.removeAt(index)

        setAdapter(this.toDos.toList())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == toDoDetailUpdateRequestCode) {
            val toDoAsString = data?.getStringExtra(toDoIdentifier)

            //update
            if (!toDoAsString.isNullOrEmpty()) {
                val gson = Gson()
                val parsedToDo = gson.fromJson<ToDo>(toDoAsString, ToDo::class.java)

                if (parsedToDo != null) {
                    updateLocalToDosWith(parsedToDo)
                }
                return
            }

            //delete
            val toDoId = data?.getIntExtra(removedToDoIdentifier,-1)

            if (toDoId == null || toDoId == -1) {
                return
            }
            removeLocalToDoWith(toDoId)


        }
        toast("Refreshed the list")
    }
}