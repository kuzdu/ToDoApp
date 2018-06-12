package rothkegel.com.todoapp.api.connector.utils

data class ToDo(
        val id: String,
        val name: String,
        val description: String,
        val expiry: Integer,
        val done: Boolean,
        val favourite: Boolean,
        val contacts: String,
        val location: String
)
