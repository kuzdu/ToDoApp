package rothkegel.com.todoapp.activities

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.todo_item.view.*
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.tools.DateTool


interface ClickListener {
    fun onDoneClicked(toDo: ToDo)
}

class ToDoListAdapter(private val toDos: ArrayList<ToDo>, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    var mCallback: ClickListener? = null

    fun setClickListenerCallback(mCallback: ClickListener) {
        this.mCallback = mCallback
    }

    override fun getItemCount(): Int {
        return toDos.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.date?.text = ""
        holder?.name?.text = ""
        holder?.description?.text = ""
        holder?.done?.isChecked = false

        holder?.date?.text = DateTool.getDateTime(toDos[position].expiry)
        holder?.name?.text = toDos[position].name
        holder?.description?.text = toDos[position].description
        holder?.done?.isChecked = toDos[position].done

        if (toDos[position].description.isNullOrBlank()) {
            holder?.description?.visibility = View.GONE
        } else {
            holder?.description?.visibility = View.VISIBLE
        }

        holder?.done?.setOnCheckedChangeListener { _, isChecked ->
            toDos[position].done = isChecked
            this.mCallback?.onDoneClicked(toDos[position])
        }
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val date = view.todo_list_item_date!!
    val name = view.todo_list_item_name!!
    val description = view.todo_list_item_description!!
    val done = view.todo_list_item_done_action!!
}