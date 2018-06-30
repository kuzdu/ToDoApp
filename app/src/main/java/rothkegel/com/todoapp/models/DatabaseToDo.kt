package rothkegel.com.todoapp.models

import rothkegel.com.todoapp.api.connector.utils.ToDo


class DatabaseToDo {
    var id: Long? = null
    var name = ""
    var description = ""
    var favourite = false
    var done = false
    var expiry: Long = -1
    var contacts = ""

    override fun toString(): String {
        return "DatabaseToDo(id=$id, name='$name', description='$description', favourite=$favourite, done=$done, expiry='$expiry')"
    }

    constructor()

    constructor(toDo: ToDo) {
        this.id = toDo.id.toLong()
        this.name = toDo.name
        this.description = toDo.description
        this.favourite = toDo.favourite
        this.done = toDo.done
        this.expiry = toDo.expiry

        val contacts = toDo.contacts

        var contactsString = ""
        if (contacts != null) {
            for (contactID in contacts) {
                contactsString += "$contactID|"
            }
        }

        this.contacts = contactsString
    }
}