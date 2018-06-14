package rothkegel.com.todoapp.tools

import rothkegel.com.todoapp.api.connector.utils.ToDo

class SortTool {
    companion object {
        fun sortByDone(toDos: Array<ToDo>, isDone: Boolean, favouriteBeforeExpiry: Boolean): List<ToDo> {
            return toDos.sortedWith(compareBy({ it.done }, { it.favourite }, { it.expiry })).reversed()
        }
    }
}