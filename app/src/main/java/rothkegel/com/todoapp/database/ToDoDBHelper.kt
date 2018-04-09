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


class ToDoDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
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
        db.insert(DBContract.TodoEntry.TABLE_NAME, null, values)
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

    /** TODO: warum sollte hier ein Array zurückgegeben werden? Das ist total bescheuert */
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

        if (cursor == null) {
            throw NullPointerException("Expression 'cursor' must not be null")
        }

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                todos.add(createToDoElement(cursor))
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

        if (cursor == null) {
            throw NullPointerException("Expression 'cursor' must not be null")
        }

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                todos.add(createToDoElement(cursor))
                cursor.moveToNext()
            }
        }
        return todos
    }

    private fun createToDoElement(cursor: Cursor): ToDo {

        val id: String = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_ID))
        val name: String = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_NAME))
        val description: String = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DESCRIPTION))
        val done: Boolean = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DONE)) > 0
        val favorite: Boolean = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_FAVORITE)) > 0
        val dueDate: String = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DUE_DATE))


        val toDo = ToDo()
        toDo.id = id
        toDo.name = name
        toDo.description = description
        toDo.done = done
        toDo.favorite = favorite
        toDo.dueDate = dueDate

        return toDo
    }

    //TODO: COLUMN_ID must be auto increment
    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FeedReader.db"

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.TodoEntry.TABLE_NAME + " (" +
                        DBContract.TodoEntry.COLUMN_ID + " TEXT PRIMARY KEY," +
                        DBContract.TodoEntry.COLUMN_NAME + " TEXT," +
                        DBContract.TodoEntry.COLUMN_DESCRIPTION + " TEXT" +
                        DBContract.TodoEntry.COLUMN_DUE_DATE + " TEXT" +
                        DBContract.TodoEntry.COLUMN_FAVORITE + " INTEGER" +
                        DBContract.TodoEntry.COLUMN_DONE + " INTEGER)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.TodoEntry.TABLE_NAME
    }
}