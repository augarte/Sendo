package augarte.sendo.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.util.TypedValue



class Utils {

    //val dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    companion object{

        private const val pattern = "yyyy/MM/dd"
        private val dateFormat: SimpleDateFormat = SimpleDateFormat(pattern)

        fun unixTimeSecondsToDate(unixTimeSecond: Int): Date {
            return Date((unixTimeSecond * 1000).toLong())
        }

        fun getUnixSeconds(): Long{
            return System.currentTimeMillis() / 1000L
        }

        fun getBiteArrayFromBitmap(bmp: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            //bmp.recycle()
            return stream.toByteArray()
        }

        fun parseDatePickerValues(year: Int, month: Int, day: Int): Date {
            val dateString = "$year/$month/$day"
            return dateFormat.parse(dateString)
        }

        fun dateToString(date: Date?): String {
            return dateFormat.format(date)
        }

        fun getColorFromAttr(context: Context?, attribute: Int): Int {
            val typedValue = TypedValue()
            val theme = context?.theme
            theme?.resolveAttribute(attribute, typedValue, true)
            return typedValue.data
        }
    }
}