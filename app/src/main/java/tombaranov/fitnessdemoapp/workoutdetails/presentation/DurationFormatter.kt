package tombaranov.fitnessdemoapp.workoutdetails.presentation

import android.content.res.Resources
import tombaranov.fitnessdemoapp.R

object DurationFormatter {

    fun format(totalMinutes: Int, resources: Resources): String {
        val hours = totalMinutes / MINUTES_IN_HOUR
        val minutes = totalMinutes % MINUTES_IN_HOUR

        return buildString {
            if (hours > 0) {
                append(resources.getQuantityString(R.plurals.hours, hours, hours))
                if (minutes > 0) append(' ')
            }
            if (minutes > 0 || (hours == 0 && minutes == 0)) {
                append(resources.getQuantityString(R.plurals.minutes, minutes, minutes))
            }
        }
    }

    private const val MINUTES_IN_HOUR = 60
}
