package rothkegel.com.todoapp.tools

import java.text.SimpleDateFormat
import java.util.*
import org.joda.time.format.DateTimeFormat


class DateTool {
    companion object {
        fun getDateTime(unixSeconds: Long): String {
            val date = java.util.Date(unixSeconds * 1000L)
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return simpleDateFormat.format(date)
        }

        //format dd/MM/yyyy HH:mm:ss
        fun convertUnixtimeToDate(dateString: String): Long {
            val formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")
            formatter.parseLocalDateTime(dateString)
            return formatter.parseDateTime(dateString).millis / 1000
        }
    }
}