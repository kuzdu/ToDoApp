package rothkegel.com.todoapp.activities

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import kotlinx.android.synthetic.main.todo_detail.*
import org.jetbrains.anko.*
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.tools.DateTool

class TodoDetailActivity : ToDoAbstractActivity() {


    var toDo = ToDo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.todo_detail)
        updateUI()
        setToDoDateClickListener()
        setToDoTimeClickListener()
        setAddContactsClickListener()
        setRemoveClickListener()
        setAddOrUpdateClickListener()
    }

    private fun setToDoTimeClickListener() {
        todo_time.setOnClickListener {
            alert {

                isCancelable = false

                lateinit var timePicker: TimePicker

                customView {
                    verticalLayout {
                        timePicker = timePicker {
                            setIs24HourView(true)
                        }
                    }
                }

                yesButton {
                    val parsedTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        "${timePicker.hour}:${timePicker.minute}"
                    } else {
                        "${timePicker.currentHour}:${timePicker.currentMinute}"
                    }
                    todo_time.text = parsedTime
                }
                noButton { }
            }.show()
        }
    }

    private fun setToDoDateClickListener() {
        todo_date.setOnClickListener {
            alert {

                isCancelable = false

                lateinit var datePicker: DatePicker

                customView {
                    verticalLayout {
                        datePicker = datePicker {
                            maxDate = System.currentTimeMillis()
                        }
                    }
                }

                yesButton {
                    val parsedDate = "${datePicker.dayOfMonth}/${datePicker.month + 1}/${datePicker.year}"
                    todo_date.text = parsedDate
                }
                noButton { }
            }.show()
        }
    }

    private fun updateUI() {
        if (isNewToDo()) {
            removeTodoButton.visibility = View.GONE
        }
    }

    private fun setAddContactsClickListener() {
        addContactsButton.setOnClickListener {
            //TODO muss noch Vorlesung anschauen
        }
    }

    private fun setRemoveClickListener() {
        removeTodoButton.setOnClickListener {
            onRemoveClickListener()
        }
    }

    private fun setAddOrUpdateClickListener() {
        addOrUpdateToDo.setOnClickListener {
            onAddOrUpdateClickListener()
        }
    }

    private fun isNewToDo(): Boolean {
        return toDo.id != -1
    }

    private fun onRemoveClickListener() {
        if (isNewToDo()) {
            alert("You really want to remove that ToDo?") {
                title = "Remove ToDo"
                yesButton { removeToDo(toDo.id) }
                noButton { }
            }.show()
        }
    }

    private fun onAddOrUpdateClickListener() {

        toDo.name = todo_name_action.text.toString()
        toDo.done = todo_is_done_action.isChecked
        toDo.favourite = todo_is_favourite_action.isChecked
        toDo.description = todo_description_action.text.toString()

        val expiryDate = todo_date.text.toString()
        val expiryTime = todo_time.text.toString()
        if (expiryDate.isNotEmpty() && expiryTime.isNotEmpty()) {


            //dd/MM/yyyy HH:mm:ss
            val dateTime = "${expiryDate} ${expiryTime}:00"
            val test =  DateTool.convertUnixtimeToDate(dateTime)
            toDo.expiry = DateTool.convertUnixtimeToDate(dateTime)
        }

        if (toDo.id == -1) {
            updateToDo(toDo)
        } else {
            addToDo(toDo)
        }
    }

}