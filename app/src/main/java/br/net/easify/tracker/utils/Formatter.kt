package br.net.easify.tracker.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class Formatter {

    companion object {
        fun currentDate(): Date {
            return Date()
        }

        fun currentDateTimeDMYAsString(): String {
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            return simpleDateFormat.format(currentDate())
        }

        fun currentDateTimeYMDAsString(): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return simpleDateFormat.format(currentDate())
        }

        fun hmsTimeFormatter(milliSeconds: Long): String? {
            return java.lang.String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds))
            )
        }
    }
}