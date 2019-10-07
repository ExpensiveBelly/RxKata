package algorithm

import arrow.core.*
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative


private sealed class ValidationError {
    object InvalidMail : ValidationError()
    object InvalidPhoneNumber : ValidationError()
}

private fun String.validatedMail(): Validated<Nel<ValidationError>, String> =
        when {
            validMail(this) -> this.valid()
            else -> ValidationError.InvalidMail.nel().invalid()
        }

private fun String.validatedPhoneNumber(): Validated<Nel<ValidationError>, String> =
        when {
            validNumber(this) -> this.valid()
            else -> ValidationError.InvalidPhoneNumber.nel().invalid()
        }

private fun validMail(mail: String) = false
private fun validNumber(number: String) = false
private fun validateData(mail: String, phoneNumber: String): Validated<Nel<ValidationError>, Pair<String, String>> {
    return Validated.applicative<Nel<ValidationError>>(Nel.semigroup()).map(mail.validatedMail(), phoneNumber.validatedPhoneNumber()) {
        Pair(it.a, it.b)
    }.fix()
}

private fun Nel<ValidationError>.handleInvalid() = map { handleInvalidField(it) }
private fun handleInvalidField(validationError: ValidationError): String =
        when (validationError) {
            ValidationError.InvalidMail -> "Invalid email"
            ValidationError.InvalidPhoneNumber -> "Invalid phone number"
        }

fun main() {
    validateData("abc@gmail.com", "1248573859").fold({ errors ->
        errors.handleInvalid().all.forEach { println(it) }
    }, { (email, phone) -> listOf(email, phone).forEach { println(it) } })
}