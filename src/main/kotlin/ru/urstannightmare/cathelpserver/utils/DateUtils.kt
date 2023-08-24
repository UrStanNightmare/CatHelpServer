package ru.urstannightmare.cathelpserver.utils

import java.time.LocalDate
import java.util.*

fun setCalendarByDate(calendar: Calendar, date: LocalDate) {
    calendar[Calendar.YEAR] = date.year
    calendar[Calendar.MONTH] = date.monthValue - 1
    calendar[Calendar.DAY_OF_MONTH] = date.dayOfMonth
}

fun countAdditionalDaysInPreviousMonth(dayOfWeek: Int): Int {
    if (dayOfWeek == Calendar.SUNDAY) {
        return 6
    }
    return if (dayOfWeek == Calendar.MONDAY) {
        0
    } else dayOfWeek - 2
}

fun countAdditionalDaysInNextMonth(dayOfWeek: Int): Int {
    if (dayOfWeek == Calendar.MONDAY) {
        return 6
    }
    return if (dayOfWeek == Calendar.SUNDAY) {
        0
    } else 8 - dayOfWeek
}

data class DateInterval(val startDate: Date, val endDate: Date)

fun countDateIntervalForSpecifiedDate(date: LocalDate): DateInterval {
    val calendar = GregorianCalendar()
    setCalendarByDate(calendar, date)
    calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
    val prevDays = countAdditionalDaysInPreviousMonth(calendar[Calendar.DAY_OF_WEEK])
    calendar.add(Calendar.DAY_OF_MONTH, -prevDays)
    val startDate = calendar.time

    setCalendarByDate(calendar, date)
    calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val pastDays = countAdditionalDaysInNextMonth(calendar[Calendar.DAY_OF_WEEK])
    calendar.add(Calendar.DAY_OF_MONTH, pastDays)
    val endDate = calendar.time

    return DateInterval(startDate, endDate)
}