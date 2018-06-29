package rothkegel.com.todoapp.activities

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.contact_item.view.*
import kotlinx.android.synthetic.main.todo_detail.*
import kotlinx.android.synthetic.main.todo_item.*
import org.jetbrains.anko.*
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.tools.DateTool


class ToDoDetailActivity : ToDoAbstractActivity() {

    var toDo = ToDo()
    val PERMISSIONS_REQUEST_READ_CONTACT = 1
    val REQUEST_PICK_UP_CONTACT = 2

    val contactIds = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_detail)
        updateUI()
        setToDoDateClickListener()
        setAddContactsClickListener()
        setRemoveClickListener()
        setAddOrUpdateClickListener()
    }


    // general operations for to do details
    private fun isNewToDo(): Boolean {
        return toDo.id == -1
    }

    // UI operations
    private fun updateUI() {
        setToDo()

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

    private fun showDateDialog() {
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

    // CLICK LISTENER
    private fun setAddContactsClickListener() {
        addContactsButton.setOnClickListener {
            onAddContactsClickListener()
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

    private fun setToDoDateClickListener() {
        todo_date.setOnClickListener {
            showDateDialog()
        }
    }

    // FINISH DETAIL ACTIVITY
    private fun finishDetailActivity(toDo: ToDo?) {
        val resultIntent = Intent()
        val gson = Gson()
        resultIntent.putExtra(toDoIdentifier, gson.toJson(toDo))
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    // ON RESPONSE METHODS
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
        finishDetailActivity(toDo)
    }

    override fun onToDoAdded(toDo: ToDo?) {
        super.onToDoAdded(toDo)
        finishDetailActivity(toDo)
    }

    private fun onAddOrUpdateClickListener() {

        toDo.name = todo_name_action.text.toString()
        toDo.description = todo_description_action.text.toString()
        toDo.done = todo_is_done_action.isChecked
        toDo.favourite = todo_is_favourite_action.isChecked

        val expiryDateTime = todo_date.text.toString()
        if (expiryDateTime.isNotEmpty()) {
            val dateTime = "$expiryDateTime:00"
            toDo.expiry = DateTool.convertUnixtimeToDate(dateTime)
        }

        //TODO: contacts fehlt noch

        if (toDo.id == -1) {
            val insertToDo = insertToDoSQL(toDo)
            if (insertToDo != null) {
                onToDoInsertSQL(insertToDo)
            }
        } else {
            updateToDoSQL(toDo)
        }
    }

    private fun onRemoveClickListener() {
        alert(getString(R.string.to_do_detail_want_remove_to_do_message)) {
            title = getString(R.string.to_do_detail_want_remove_to_do_title)
            yesButton { removeToDoSQL(toDo.id) }
            noButton { }
        }.show()
    }

    //CHECK CONTACT PERMISSIONS
    private fun onAddContactsClickListener() {
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                        Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACT)
            //callback onRequestPermissionsResult
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            startActivityForResult(intent, REQUEST_PICK_UP_CONTACT)
        }
    }


    private fun getCursor(data: Uri?): Cursor? {
        val contactData = data
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(contactData, null, null, null, null)

        if (cursor == null) {
            toast("Find no cursor")
            return null
        }
        return cursor
    }


    private interface ProfileQuery {
        companion object {
            val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)

            val ADDRESS = 0
            val IS_PRIMARY = 1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PICK_UP_CONTACT) {
            if (resultCode == RESULT_OK) {

                val cursor = getCursor(data?.data)


                cursor?.moveToFirst()

                val mailAddress = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS))
                val phoneNumber = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val name = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val contactId = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Identity.CONTACT_ID))


                val view = LayoutInflater.from(applicationContext).inflate(R.layout.contact_item, null)

                if (phoneNumber.isNullOrEmpty()) {
                    view.phoneNumber.visibility = View.GONE
                }

                if (mailAddress.isNullOrEmpty()) {
                    view.mailAddress.visibility = View.GONE
                }

                if (name.isNullOrEmpty()) {
                    view.contactName.visibility = View.GONE
                }

                if (contactId.isNullOrEmpty()) {
                    toast("Fehler - Es konnte keine Kontakt-ID gefunden werden.")
                    return
                }

                if (contactId == null) {
                    toast("Fehler - keine Kontakt-Id gefunden")
                    return
                }

                contactIds.add(contactId)
                this.toDo.contacts = contactIds.toTypedArray()

                view.removeContactButton.setOnClickListener {
                    contactIds.remove(contactId)
                    this.toDo.contacts = contactIds.toTypedArray()
                    contactList.removeView(view)
                }

                val toDoName = if (todo_name_action.text.toString().isNotEmpty()) todo_name_action.text.toString() else toDo.name
                val toDoDescription = if (todo_description_action.text.toString().isNotEmpty()) todo_description_action.text.toString() else toDo.description

                view.contactName.text = name
                view.phoneNumber.text = phoneNumber
                view.phoneNumber.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
                    intent.putExtra("sms_body", "Hallo ich bin das Todo \"{$toDoName}\" - {$toDoDescription}")
                    startActivity(intent)
                }

                view.mailAddress.text = mailAddress

                view.mailAddress.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_EMAIL, mailAddress)
                    intent.putExtra(Intent.EXTRA_SUBJECT, toDoName)
                    intent.putExtra(Intent.EXTRA_TEXT, toDoDescription)
                    startActivity(Intent.createChooser(intent, "Email senden"))
                }

                contactList.addView(view)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onAddContactsClickListener()
            } else {
                toast("Permission must be granted in order to display contacts information")
            }
        }
    }
}