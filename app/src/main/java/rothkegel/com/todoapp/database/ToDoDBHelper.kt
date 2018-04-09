package rothkegel.com.todoapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import rothkegel.com.todoapp.models.ToDo
import java.util.*


class UsersDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }


    @Throws(SQLiteConstraintException::class)
    fun insertTodo(todo: ToDo): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.TodoEntry.COLUMN_ID, todo.id)
        values.put(DBContract.TodoEntry.COLUMN_NAME, todo.name)
        values.put(DBContract.TodoEntry.COLUMN_DESCRIPTION, todo.description)
        values.put(DBContract.TodoEntry.COLUMN_FAVORITE, todo.favorite)
        values.put(DBContract.TodoEntry.COLUMN_DONE, todo.done)
        values.put(DBContract.TodoEntry.COLUMN_DUE_DATE, todo.dueDate)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.TodoEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteToDo(id: String): Boolean {
        val db = writableDatabase
        val selection = DBContract.TodoEntry.COLUMN_ID + " LIKE ?"
        val selectionArgs = arrayOf(id)
        db.delete(DBContract.TodoEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun readToDo(id: String): ArrayList<ToDo> {
        val todos = ArrayList<ToDo>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.TodoEntry.TABLE_NAME + " WHERE " + DBContract.TodoEntry.COLUMN_ID + "='" + id + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var id: String
        var name: String
        var description: String
        var done: Boolean
        var favorite: Boolean
        var dueDate: String

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_ID))
                name = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_NAME))
                description = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DESCRIPTION))
                done = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DONE)) > 0
                favorite = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_FAVORITE)) > 0
                dueDate = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DUE_DATE))


                val element = ToDo()
                element.id = id
                element.name = name
                element.description = description
                element.done = done
                element.favorite = favorite
                element.dueDate = dueDate

                todos.add(element)
                cursor.moveToNext()
            }
        }
        return todos
    }

    fun readAllTodos(): ArrayList<ToDo> {
        val todos = ArrayList<ToDo>()
        val db = writableDatabase
        var cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " + DBContract.TodoEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var id: String
        var name: String
        var description: String
        var done: Boolean
        var favorite: Boolean
        var dueDate: String

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                id = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_ID))
                name = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_NAME))
                description = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DESCRIPTION))
                done = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DONE)) > 0
                favorite = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_FAVORITE)) > 0
                dueDate = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DUE_DATE))


                val element = ToDo()
                element.id = id
                element.name = name
                element.description = description
                element.done = done
                element.favorite = favorite
                element.dueDate = dueDate

                todos.add(element)
                cursor.moveToNext()
            }
        }
        return todos
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FeedReader.db"

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.TodoEntry.TABLE_NAME + " (" +
                        DBContract.TodoEntry.COLUMN_ID + " TEXT PRIMARY KEY," +
                        DBContract.TodoEntry.COLUMN_NAME + " TEXT," +
                        DBContract.TodoEntry.COLUMN_DESCRIPTION + " TEXT)" +
                        DBContract.TodoEntry.COLUMN_DUE_DATE + " DATE)" +
                        DBContract.TodoEntry.COLUMN_FAVORITE + " BOOL)" +
                        DBContract.TodoEntry.COLUMN_DONE + " BOOL)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.TodoEntry.TABLE_NAME
    }
}