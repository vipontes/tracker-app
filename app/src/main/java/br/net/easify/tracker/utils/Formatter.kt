package br.net.easify.tracker.utils

import java.text.SimpleDateFormat
import java.util.*

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
    }
}