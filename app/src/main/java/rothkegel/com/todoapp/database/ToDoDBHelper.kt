package rothkegel.com.todoapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import rothkegel.com.todoapp.api.connector.utils.ToDo
import rothkegel.com.todoapp.models.DatabaseToDo
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
    fun insertToDo(databaseToDo: DatabaseToDo): Long {
        val db = writableDatabase

        val values = ContentValues()
        values.put(DBContract.TodoEntry.COLUMN_NAME, databaseToDo.name)
        values.put(DBContract.TodoEntry.COLUMN_DESCRIPTION, databaseToDo.description)
        values.put(DBContract.TodoEntry.COLUMN_FAVOURITE, databaseToDo.favourite)
        values.put(DBContract.TodoEntry.COLUMN_DONE, databaseToDo.done)
        values.put(DBContract.TodoEntry.COLUMN_EXPIRY, databaseToDo.expiry)
        values.put(DBContract.TodoEntry.COLUMN_CONTACTS, databaseToDo.contacts)

        return db.insert(DBContract.TodoEntry.TABLE_NAME, null, values)
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteById(id: Long): Boolean {
        val db = writableDatabase
        return db.delete(DBContract.TodoEntry.TABLE_NAME, DBContract.TodoEntry.COLUMN_ID + "=" + id, null) > 0
    }


    fun changeToDo(changedDatabaseToDo: DatabaseToDo): Boolean {

        val db = writableDatabase
        val values = ContentValues()

        values.put(DBContract.TodoEntry.COLUMN_NAME, changedDatabaseToDo.name)
        values.put(DBContract.TodoEntry.COLUMN_DESCRIPTION, changedDatabaseToDo.description)
        values.put(DBContract.TodoEntry.COLUMN_FAVOURITE, changedDatabaseToDo.favourite)
        values.put(DBContract.TodoEntry.COLUMN_DONE, changedDatabaseToDo.done)
        values.put(DBContract.TodoEntry.COLUMN_CONTACTS, changedDatabaseToDo.contacts)
        values.put(DBContract.TodoEntry.COLUMN_EXPIRY, changedDatabaseToDo.expiry)

        return db.update(DBContract.TodoEntry.TABLE_NAME, values, DBContract.TodoEntry.COLUMN_ID + "=" + changedDatabaseToDo.id, null) > 0
    }

    fun readToDoById(id: Long): DatabaseToDo? {

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

    fun readAllTodos(): ArrayList<DatabaseToDo> {
        val toDos = ArrayList<DatabaseToDo>()
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

    private fun createToDoElement(cursor: Cursor): DatabaseToDo {

        val id = cursor.getLong(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_NAME))
        val description = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DESCRIPTION))
        val done = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_DONE)) > 0
        val favorite = cursor.getInt(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_FAVOURITE)) > 0
        val contacts = cursor.getString(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_CONTACTS))
        val expiry = cursor.getLong(cursor.getColumnIndex(DBContract.TodoEntry.COLUMN_EXPIRY))

        val databaseToDo = DatabaseToDo()
        databaseToDo.id = id
        databaseToDo.name = name
        databaseToDo.description = description
        databaseToDo.done = done
        databaseToDo.favourite = favorite
        databaseToDo.contacts = contacts
        databaseToDo.expiry = expiry

        return databaseToDo
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 12
        const val DATABASE_NAME = "ToDo.db"

        private const val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.TodoEntry.TABLE_NAME + " (" +
                        DBContract.TodoEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        DBContract.TodoEntry.COLUMN_NAME + " TEXT," +
                        DBContract.TodoEntry.COLUMN_DESCRIPTION + " TEXT," +
                        DBContract.TodoEntry.COLUMN_EXPIRY + " INTEGER," +
                        DBContract.TodoEntry.COLUMN_FAVOURITE + " INTEGER," +
                        DBContract.TodoEntry.COLUMN_CONTACTS + " TEXT," +
                        DBContract.TodoEntry.COLUMN_DONE + " INTEGER" + ")"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.TodoEntry.TABLE_NAME
    }
}