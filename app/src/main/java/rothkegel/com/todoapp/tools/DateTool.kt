package rothkegel.com.todoapp.tools

import java.text.SimpleDateFormat
import java.util.*


class DateTool {
    companion object {
        fun getDateTime(unixSeconds: Int): String {
            val date = java.util.Date(unixSeconds * 1000L)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
            return simpleDateFormat.format(date)
        }
    }
}