package rothkegel.com.todoapp.activities

import android.content.Context
import android.support.v4.content.ContextCompat
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
    fun onFavouriteClicked(toDo: ToDo)
    fun onToDoItemClicked(position: Int)
}

class ToDoListAdapter(private val toDos: ArrayList<ToDo>, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    private var clickListenerCallback: ClickListener? = null

    fun setClickListenerCallback(mCallback: ClickListener) {
        this.clickListenerCallback = mCallback
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


        if (DateTool.isExpired(toDos[position].expiry) && !toDos[position].done) {
            holder?.date?.text = DateTool.getDateTime(toDos[position].expiry) + "(spät dran!)"
            holder?.date?.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
        } else {
            holder?.date?.text = DateTool.getDateTime(toDos[position].expiry)
            holder?.date?.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
        }
        holder?.name?.text = toDos[position].name
        holder?.description?.text = toDos[position].description


        if (toDos[position].description.isNullOrBlank()) {
            holder?.description?.visibility = View.GONE
        } else {
            holder?.description?.visibility = View.VISIBLE
        }

        holder?.done?.setOnCheckedChangeListener(null)
        holder?.done?.isChecked = toDos[position].done

        holder?.done?.setOnCheckedChangeListener { _, isChecked ->
            toDos[position].done = isChecked
            this.clickListenerCallback?.onDoneClicked(toDos[position])
        }


        holder?.favourite?.setOnClickListener(null)
        if (toDos[position].favourite) {
            holder?.favourite?.setImageResource(R.drawable.ic_star_black_24dp)
        } else {
            holder?.favourite?.setImageResource(R.drawable.ic_star_border_black_24dp)
        }

        holder?.favourite?.setOnClickListener {
            toDos[position].favourite = !toDos[position].favourite
            this.clickListenerCallback?.onFavouriteClicked(toDos[position])
        }

        holder?.wholeItem?.setOnClickListener(null)

        holder?.wholeItem?.setOnClickListener {
            this.clickListenerCallback?.onToDoItemClicked(position)
        }

    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val date = view.todo_list_item_date!!
    val name = view.todo_list_item_name!!
    val description = view.todo_list_item_description!!
    val done = view.todo_list_item_done_action!!
    val favourite = view.todo_list_item_favourite_action!!
    val wholeItem = view.todo_list_single_item!!
}