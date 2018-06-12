package rothkegel.com.todoapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import rothkegel.com.todoapp.models.ToDoOld
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
    fun insertTodo(todo: ToDoOld): Long {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DBContract.TodoEntry.COLUMN_NAME, todo.name)
        values.put(DBContract.TodoEntry.COLUMN_DESCRIPTION, todo.description)
        values.put(DBContract.TodoEntry.COLUMN_FAVORITE, todo.favorite)
        values.put(DBContract.TodoEntry.COLUMN_DONE, todo.done)
        values.put(DBContract.TodoEntry.COLUMN_DUE_DATE, todo.dueDate)

        return db.insert(DBContract.TodoEntry.TABLE_NAME, null, values)
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteById(id: Long): Boolean {
        val db = writableDatabase
        return db.delete(DBContract.TodoEntry.TABLE_NAME, DBContract.TodoEntry.COLUMN_ID + "=" + id, null) > 0
    }


    fun changeToDo(changedToDoOld: ToDoOld): Boolean {

        val db = writableDatabase
        val values = ContentValues()

        values.put(DBContract.TodoEntry.COLUMN_NAME, changedToDoOld.name)
        values.put(DBContract.TodoEntry.COLUMN_DESCRIPTION, changedToDoOld.description)
        values.put(DBContract.TodoEntry.COLUMN_FAVORITE, changedToDoOld.favorite)
        values.put(DBContract.TodoEntry.COLUMN_DONE, changedToDoOld.done)
        values.put(DBContract.TodoEntry.COLUMN_DUE_DATE, changedToDoOld.dueDate)

        return db.update(DBContract.TodoEntry.TABLE_NAME, values, DBContract.TodoEntry.COLUMN_ID + "=" + changedToDoOld.id, null) > 0
    }

    fun readToDoById(id: Long): ToDoOld? {

        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.TodoEntry.TABLE_NAME + " WHERE " + DBContract.TodoEntry.COLUMN_ID + "='" + id + "'", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return null
        }

        if (cursor == null) {
            throw NullPointerException("Expression 'cursor' must not be null")
        }

        if (cursor.moveToFirst()) {
            return createToDoElement(cursor)
        }

        return null
    }

    fun readAllTodos(): ArrayList<ToDoOld> {
        val toDos = ArrayList<ToDoOld>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.TodoEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        if (cursor == null) {
            throw NullPointerException("Expression 'cursor' must not be null")
        }

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                toDos.add(createToDoElement(cursor))
                cursor.moveToNext()
            }
        }
        return toDos
    }

    private fun createToDoElement(cursor: Cursor): ToDoOld {

        val id = cursor.getLong(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_NAME))
        val description = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DESCRIPTION))
        val done = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DONE)) > 0
        val favorite = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_FAVORITE)) > 0
        val dueDate = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DUE_DATE))

        val toDo = ToDoOld()
        toDo.id = id
        toDo.name = name
        toDo.description = description
        toDo.done = done
        toDo.favorite = favorite
        toDo.dueDate = dueDate

        return toDo
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 11
        const val DATABASE_NAME = "ToDo.db"

        private const val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.TodoEntry.TABLE_NAME + " (" +
                        DBContract.TodoEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.TodoEntry.COLUMN_NAME + " TEXT," +
                        DBContract.TodoEntry.COLUMN_DESCRIPTION + " TEXT," +
                        DBContract.TodoEntry.COLUMN_DUE_DATE + " TEXT," +
                        DBContract.TodoEntry.COLUMN_FAVORITE + " INTEGER," +
                        DBContract.TodoEntry.COLUMN_DONE + " INTEGER" + ")"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.TodoEntry.TABLE_NAME
    }
}