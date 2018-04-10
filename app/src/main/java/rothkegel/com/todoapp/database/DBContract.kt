package rothkegel.com.todoapp.database

import android.provider.BaseColumns


object DBContract {

    /* Inner class that defines the table contents */
    class TodoEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "todos"
            const val COLUMN_ID = "id"
            const val COLUMN_NAME = "name"
            const val COLUMN_DESCRIPTION = "description"
            const val COLUMN_DONE = "done"
            const val COLUMN_FAVORITE = "favorite"
            const val COLUMN_DUE_DATE = "due_date"
        }
    }
}