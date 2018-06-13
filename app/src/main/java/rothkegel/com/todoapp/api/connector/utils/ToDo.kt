package rothkegel.com.todoapp.api.connector.utils

data class ToDo(
        var id: Int,
        var name: String,
        var description: String,
        var expiry: Int,
        var done: Boolean,
        var favourite: Boolean,
        var contacts: Array<String>?,
        var location: Location?
) {
    constructor() : this(-1,"","",-1,false,false,null,null)
}
