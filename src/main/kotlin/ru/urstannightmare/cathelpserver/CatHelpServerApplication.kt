package ru.urstannightmare.cathelpserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CatHelpServerApplication

fun main(args: Array<String>) {
	runApplication<CatHelpServerApplication>(*args)
}
