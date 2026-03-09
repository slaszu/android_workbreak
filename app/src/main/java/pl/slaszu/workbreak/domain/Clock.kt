package pl.slaszu.workbreak.domain

import java.time.LocalDateTime

interface Clock {
    fun getNow(): LocalDateTime
}