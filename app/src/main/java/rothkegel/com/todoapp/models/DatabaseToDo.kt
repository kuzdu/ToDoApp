package rothkegel.com.todoapp.models


class DatabaseToDo {
    var id:Long = -1
    var name = ""
    var description = ""
    var favourite = false
    var done = false
    var expiry:Long = -1
    var contacts = ""

    override fun toString(): String {
        return "DatabaseToDo(id=$id, name='$name', description='$description', favourite=$favourite, done=$done, expiry='$expiry')"
    }
}