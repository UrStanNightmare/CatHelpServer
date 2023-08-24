package ru.urstannightmare.cathelpserver.configuration.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "server")
class OriginsProperty {
    var origins: List<String> = mutableListOf()
        get() = field.ifEmpty { listOf("*") }
}