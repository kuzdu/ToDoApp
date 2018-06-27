package rothkegel.com.todoapp.activities

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.todo_detail.*
import org.jetbrains.anko.*
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.tools.DateTool
import android.content.Intent


class ToDoDetailActivity : ToDoAbstractActivity() {


    var toDo = ToDo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_detail)

        setToDo()


        updateUI()
        setToDoDateClickListener()
        setAddContactsClickListener()
        setRemoveClickListener()
        setAddOrUpdateClickListener()
    }

    private fun setToDo() {
        val gson = Gson()

        val toDoAsString = intent.getStringExtra(toDoIdentifier)
        if (toDoAsString.isNullOrEmpty()) {
            return
        }
        val parsedToDo = gson.fromJson<ToDo>(toDoAsString, ToDo::class.java)
        if (parsedToDo != null) {
            this.toDo = parsedToDo
        }
    }

    private fun showTimeDialog() {
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
                todo_date.setText("${todo_date.text} ${parsedTime}", TextView.BufferType.EDITABLE)
            }
            noButton { }
        }.show()
    }

    private fun setToDoDateClickListener() {
        todo_date.setOnClickListener {
            alert {

                isCancelable = false

                lateinit var datePicker: DatePicker

                customView {
                    verticalLayout {
                        datePicker = datePicker {
                            minDate = System.currentTimeMillis()
                        }
                    }
                }

                yesButton {
                    val parsedDate = "${datePicker.dayOfMonth}/${datePicker.month + 1}/${datePicker.year}"
                    todo_date.setText(parsedDate, TextView.BufferType.EDITABLE)
                    showTimeDialog()
                }
                noButton { }
            }.show()
        }
    }

    private fun updateUI() {
        if (isNewToDo()) {
            removeTodoButton.visibility = View.GONE
            return
        }


        todo_name_action.setText(toDo.name, TextView.BufferType.EDITABLE)
        todo_description_action.setText(toDo.description, TextView.BufferType.EDITABLE)
        todo_date.setText(DateTool.getDateTime(toDo.expiry), TextView.BufferType.EDITABLE)
        todo_is_done_action.isChecked = toDo.done
        todo_is_favourite_action.isChecked = toDo.favourite
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
        return toDo.id == -1
    }

    private fun onRemoveClickListener() {
        alert("You really want to remove that ToDo?") {
            title = "Remove ToDo"
            yesButton { removeToDo(toDo.id) }
            noButton { }
        }.show()
    }

    private fun onAddOrUpdateClickListener() {

        toDo.name = todo_name_action.text.toString()
        toDo.done = todo_is_done_action.isChecked
        toDo.favourite = todo_is_favourite_action.isChecked
        toDo.description = todo_description_action.text.toString()

        val expiryDateTime = todo_date.text.toString()
        if (expiryDateTime.isNotEmpty()) {
            val dateTime = "$expiryDateTime:00"
            toDo.expiry = DateTool.convertUnixtimeToDate(dateTime)
        }

        if (toDo.id == -1) {
            addToDo(toDo)
        } else {
            updateToDo(toDo)
        }
    }

    override fun onToDoRemoved(removed: Boolean?) {
        super.onToDoRemoved(removed)
        if (removed != null && removed) {
            val resultIntent = Intent()
            resultIntent.putExtra(removedToDoIdentifier, toDo.id)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            toast("Beim Löschen ist ein Fehler aufgetreten. - ¯\\_(ツ)_/¯")
        }
    }

    override fun onToDoUpdated(toDo: ToDo?) {
        super.onToDoUpdated(toDo)
        finishDetailActivitiy(toDo)
    }

    override fun onToDoAdded(toDo: ToDo?) {
        super.onToDoAdded(toDo)
        finishDetailActivitiy(toDo)
    }

    private fun finishDetailActivitiy(toDo: ToDo?) {
        val resultIntent = Intent()
        val gson = Gson()
        resultIntent.putExtra(toDoIdentifier, gson.toJson(toDo))
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}