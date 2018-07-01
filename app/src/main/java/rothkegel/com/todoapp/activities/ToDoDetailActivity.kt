package rothkegel.com.todoapp.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.contact_item.view.*
import kotlinx.android.synthetic.main.todo_detail.*
import org.jetbrains.anko.*
import rothkegel.com.todoapp.R
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.tools.DateTool


class ToDoDetailActivity : ToDoAbstractActivity() {

    var toDo = ToDo()
    val PERMISSIONS_REQUEST_READ_CONTACT = 1
    val REQUEST_PICK_UP_CONTACT = 2
    val PERMISSIONS_READ_ALL_CONTACTS = 3

    val contactIds = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_detail)
        updateUI()
        setToDoDateClickListener()
        setAddContactsClickListener()
        setRemoveClickListener()
        setAddOrUpdateClickListener()

        if (!needContactPermission()) {
            loadContacts()
        }
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
                    "${formattedDateTime(timePicker.hour)}:${formattedDateTime(timePicker.minute)}"
                } else {
                    "${formattedDateTime(timePicker.currentHour)}:${formattedDateTime(timePicker.currentMinute)}"
                }
                todo_date.setText("${todo_date.text} ${parsedTime}", TextView.BufferType.EDITABLE)
            }
            noButton {
                todo_date.setText("", TextView.BufferType.EDITABLE)
            }
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

                val dayOfMonth = formattedDateTime(datePicker.dayOfMonth)
                val month = formattedDateTime(datePicker.month + 1)
                val year = formattedDateTime(datePicker.year)

                val parsedDate = "${dayOfMonth}/${month}/${year}"

                todo_date.setText(parsedDate, TextView.BufferType.EDITABLE)
                showTimeDialog()
            }
            noButton {
                todo_date.setText("", TextView.BufferType.EDITABLE)
            }
        }.show()
    }

    private fun formattedDateTime(unit: Int): String {
        if (unit < 10) {
            return "0$unit"
        }
        return "$unit"
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

        if (todo_name_action.text.toString().isNullOrEmpty() || todo_date.text.toString().isNullOrEmpty()) {
            toast("Ein todo muss Name einen Namen und eine Zeitangabe haben.")
            return
        }

        toDo.name = todo_name_action.text.toString()
        toDo.description = todo_description_action.text.toString()
        toDo.done = todo_is_done_action.isChecked
        toDo.favourite = todo_is_favourite_action.isChecked

        val expiryDateTime = todo_date.text.toString()
        if (expiryDateTime.isNotEmpty()) {

            var dateTime = expiryDateTime

            if (expiryDateTime.length < 19) {
                dateTime += ":00"
            }
            toDo.expiry = DateTool.convertUnixtimeToDate(dateTime)
        }

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

        if (needContactPermission()) {
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_READ_ALL_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                toast("Fehler - kein Recht dazu")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == REQUEST_PICK_UP_CONTACT) {
            if (resultCode == RESULT_OK) {

                val cursor = getCursor(data?.data)
                cursor?.moveToFirst()

                // val mailAddress = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS))
                val phoneNumber = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val name = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val contactId = cursor?.getString(cursor?.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Identity.CONTACT_ID))

                if (contactId == null) {
                    toast("Fehler - ID konnte für den Kontakt nicht gefunden werden.")
                    return
                }

                val mailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(contactId), null)

                var mailAddress = ""
                while (mailCursor.moveToNext()) {
                    mailAddress = mailCursor.getString(mailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                }
                mailCursor.close()

                addContactToList(phoneNumber, mailAddress, name, contactId)
            }
        }
    }

    private fun addContactToList(phoneNumber: String?, mailAddress: String?, name: String?, contactId: String?) {
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


        view.contactName.text = name
        view.phoneNumber.text = phoneNumber
        view.phoneNumber.setOnClickListener {
            val toDoName = if (todo_name_action.text.toString().isNotEmpty()) todo_name_action.text.toString() else toDo.name
            val toDoDescription = if (todo_description_action.text.toString().isNotEmpty()) todo_description_action.text.toString() else toDo.description


            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
            intent.putExtra("sms_body", "Hallo ich bin das Todo $toDoName - $toDoDescription")
            startActivity(intent)
        }

        view.mailAddress.text = mailAddress

        view.mailAddress.setOnClickListener {

            val toDoName = if (todo_name_action.text.toString().isNotEmpty()) todo_name_action.text.toString() else toDo.name
            val toDoDescription = if (todo_description_action.text.toString().isNotEmpty()) todo_description_action.text.toString() else toDo.description

            val mailto = "mailto:$mailAddress" +
                    "?cc=" + "" +
                    "&subject=" + Uri.encode(toDoName) +
                    "&body=" + Uri.encode(toDoDescription)

            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse(mailto)

            try {
                startActivity(emailIntent)
            } catch (e: ActivityNotFoundException) {
                toast("Es wurde keine App gefunden mit der du Mails verschicken kannst.")
            }
        }

        contactList.addView(view)
    }

    private fun loadContacts() {
        if (needContactPermission()) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_READ_ALL_CONTACTS)
        } else {
            addExistingContacts()
        }
    }

    private fun needContactPermission() =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED


    private fun addExistingContacts() {
        val toDoContacts = toDo.contacts ?: return

        val resolver = contentResolver
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cursor.count <= 0) {
            toast("Fehler - keine Kontakte auf dem Telefon gefunden.")
            return
        }

        var mailAddress = ""

        val phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        val preventSameContactEntries = arrayListOf<String>()

        while (phoneCursor.moveToNext()) {

            val contactId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))

            if (!toDoContacts.toList().contains(contactId)) {
                continue
            }

            val name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val mailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf<String>(contactId), null)

            while (mailCursor.moveToNext()) {
                mailAddress = mailCursor.getString(mailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
            }
            mailCursor.close()

            if (toDoContacts.toList().contains(contactId) && !preventSameContactEntries.contains(contactId)) {
                preventSameContactEntries.add(contactId)
                addContactToList(phoneNumber, mailAddress, name, contactId)
            }
        }
        phoneCursor.close()
    }
}