package pl.slaszu.workbreak.infrastructure.system

import jakarta.inject.Inject
import pl.slaszu.workbreak.domain.Clock
import java.time.LocalDateTime
import java.time.ZoneId

class ClockImpl @Inject constructor() : Clock {
    override fun getNow(): LocalDateTime {
        return LocalDateTime.now(ZoneId.systemDefault())
    }
}