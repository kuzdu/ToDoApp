package rothkegel.com.todoapp.tools

import rothkegel.com.todoapp.api.connector.utils.ToDo

class SortTool {
    companion object {

        fun getSortedByDoneThenFavouriteThenExpiry(toDos: ArrayList<ToDo>): ArrayList<ToDo> {
            return SortTool.sortByDone(toDos, true, true)
        }

        fun getSortedByDoneThenExpiryThenFavourite(toDos: ArrayList<ToDo>): ArrayList<ToDo> {
            return SortTool.sortByDone(toDos, true, false)
        }

        fun getSortedByNonDoneThenFavouriteThenExpiry(toDos: ArrayList<ToDo>): ArrayList<ToDo> {
            return SortTool.sortByDone(toDos, false, true)
        }

        fun getSortedByNonDoneThenExpiryThenFavourite(toDos: ArrayList<ToDo>): ArrayList<ToDo> {
            return SortTool.sortByDone(toDos, false, false)
        }

        private fun sortByDone(toDos: ArrayList<ToDo>, isDone: Boolean, favouriteBeforeExpiry: Boolean): ArrayList<ToDo> {
            return if (favouriteBeforeExpiry) {
                ArrayList(toDos.sortedWith(compareBy({ it.done == isDone }, { it.favourite }, { it.expiry })).reversed())
            } else {
                ArrayList(toDos.sortedWith(compareBy({ it.done == isDone }, { it.expiry }, { it.favourite })).reversed())
            }
        }
    }
}