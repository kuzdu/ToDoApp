package rothkegel.com.todoapp.database

import android.provider.BaseColumns


object DBContract {

    /* Inner class that defines the table contents */
    class TodoEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "todos"
            val COLUMN_ID = "id"
            val COLUMN_NAME = "name"
            val COLUMN_DESCRIPTION = "description"
            val COLUMN_DONE = "done"
            val COLUMN_FAVORITE = "favorite"
            val COLUMN_DUE_DATE = "due_date"
        }
    }
}