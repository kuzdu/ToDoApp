package rothkegel.com.todoapp.tools

import rothkegel.com.todoapp.api.connector.utils.ToDo

class SortTool {
    companion object {

        fun getSortedByDoneThenFavouriteThenExpiry(toDos: Array<ToDo>): List<ToDo> {
            return SortTool.sortByDone(toDos, true, true)
        }

        fun getSortedByDoneThenExpiryThenFavourite(toDos: Array<ToDo>): List<ToDo> {
            return SortTool.sortByDone(toDos, true, false)
        }

        fun getSortedByDoneFalseThenFavouriteThenExpiry(toDos: Array<ToDo>): List<ToDo> {
            return SortTool.sortByDone(toDos, false, true)
        }

        fun getSortedByDoneFalseThenExpiryThenFavourite(toDos: Array<ToDo>): List<ToDo> {
            return SortTool.sortByDone(toDos, false, false)
        }

        private fun sortByDone(toDos: Array<ToDo>, isDone: Boolean, favouriteBeforeExpiry: Boolean): List<ToDo> {

            return if (favouriteBeforeExpiry) {
                toDos.sortedWith(compareBy({ it.done == isDone }, { it.favourite }, { it.expiry })).reversed()
            } else {
                toDos.sortedWith(compareBy({ it.done == isDone }, { it.expiry }, { it.favourite })).reversed()
            }
        }
    }
}