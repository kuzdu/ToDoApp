package rothkegel.com.todoapp.models


class ToDo {
    var id:Long = -1
    var name = ""
    var description = ""
    var favorite = false
    var done = false
    var dueDate = ""

    override fun toString(): String {
        return "ToDo(id=$id, name='$name', description='$description', favorite=$favorite, done=$done, dueDate='$dueDate')"
    }
}