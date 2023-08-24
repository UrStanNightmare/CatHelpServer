package ru.urstannightmare.cathelpserver.configuration

import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import ru.urstannightmare.cathelpserver.configuration.security.AuthException
import ru.urstannightmare.cathelpserver.task.TaskNotFoundException
import ru.urstannightmare.cathelpserver.task.v2.dto.ErrorResponse
import java.util.stream.Collectors

@RestControllerAdvice
class ControllerAdvice {
    private val log = LoggerFactory.getLogger(ControllerAdvice::class.java)
    @ExceptionHandler(value = [TaskNotFoundException::class])
    fun dataRetrievalFailureException(
        ex: TaskNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.message ?: "No message",
                request.getDescription(false)
            ),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(value = [MissingServletRequestParameterException::class])
    fun handleMussingParameterException(
        ex: MissingServletRequestParameterException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("Handled ${request.getDescription(false)} request with missing ${ex.parameterName} parameter.")

        return ResponseEntity(
            ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Missing [${ex.parameterName}] request parameter",
                request.getDescription(false)
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(
        value = [
            MethodArgumentTypeMismatchException::class,
            HttpMessageNotReadableException::class,
            ConstraintViolationException::class
        ]
    )
    fun handleIncorrectRequestParameterException(
        ex: RuntimeException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {

        var parameterName: Any? = when (ex) {
            is MethodArgumentTypeMismatchException -> ex.value
            is HttpMessageNotReadableException -> "requestBody"
            is ConstraintViolationException -> ex.constraintViolations
                .stream()
                .map { v -> v.propertyPath.toString()}
                .collect(Collectors.joining(","))

            else -> null
        }

        log.warn("Handled ${request.getDescription(false)} request with incorrect [$parameterName] parameter.")

        return ResponseEntity(
            ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Incorrect request value [$parameterName]",
                request.getDescription(false)
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(
        value = [
            AuthException::class
        ]
    )
    fun handleAuthException(
        ex: RuntimeException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse>{
        log.warn("Handled bad auth attempt ${request.getDescription(false)}. {}", ex.message)

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Auth error. ${ex.message}", ""))
    }
}