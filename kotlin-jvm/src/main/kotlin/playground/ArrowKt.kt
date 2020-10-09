@file:Suppress("PackageDirectoryMismatch")

package playground.arrowkt

import arrow.core.*
import java.lang.NumberFormatException

/**
 * Manual Dependency Injection: Pure Kotlin, no framework required Dependency Injection
 * ideal for smaller-mid level projects which can be migrated easily to Dagger|Hilt like
 * framework.
 *
 * - [Blog](https://proandroiddev.com/hold-on-before-you-dagger-or-hilt-try-this-simple-di-f674c83ebeec)
 * - [Github-Sample](https://github.com/ch8n/android-no-3rd-party-mvvm-example)
 * - [Author](https://chetangupta.net)
 */
fun main() {
    println()
    println("# Arrow-Kt Sample")
    OptionDemo.demo()
    println()
    EitherDemo.demo()
    println()
    ValidateDemo.demo()

}

/**
 *  used for having all errors reported simultaneously.
 * **/
object ValidateDemo {
    fun demo() {
        creation()
    }

    fun creation() {
        open class Errors {}
        class NumberFormatError : Errors()
        class IllegalValueError : Errors()

        val result1: Validated</*Errors*/Errors,/*value*/String> = IllegalValueError().invalid()
        println(" is valid? ${result1.isValid}, ${result1} ")
        val result2: Validated</*Errors*/Errors,/*value*/String> = "pokemon".valid()
        println(" is valid? ${result2.isValid}, ${result2} ")
    }

}


/**
 * Wrapper for writing functions that can fail
 * Either is used to short-circuit a computation upon the first error
 * Either is right-biased, i.e right contains the value and left contains error
 */
object EitherDemo {
    fun demo() {
        println("# --- ArrowKt-Either-type-examples ---")
        correctValue()
        errorValue()
        usingEitherForExceptionHandling()
        generalOperationOnEither()
        defaultValueHandling()
        conditionalValue()
    }

    fun correctValue() {
        println("# Either example 1")
        val someValue: Either</*errorMsg*/String,/*value*/String> = Either.right("Sample value")
        val newValue = someValue.flatMap {
            val mutation = StringBuilder().append(it).append("***").append(it).toString()
            Either.right(mutation)
        }
        println("initValue : $someValue \nAfter transformation: ${newValue.right()}")
    }

    fun errorValue() {
        println("# Either example 2")
        // if something is set left then perform action on the rightly value will result in nothing
        val left: Either<String, Int> = Either.Left("Something went wrong")
        val value = left.flatMap { Either.Right(it + 1) }
        println("initValue : $left \nAfter transformation: $value")
    }

    fun usingEitherForExceptionHandling() {
        println("# You can use Either for ExceptionHandling")
        val somevalue = "xyz"
        val someAction = somevalue.toIntOrNull()
        val result = if (someAction == null) {
            Either.Left(NumberFormatException("not a number"));
        } else {
            Either.Right(someAction)
        }
        println("initValue : $somevalue \nAfter transformation: $result")
    }

    fun generalOperationOnEither() {
        println("# General Either operation examples")

        println("# swap left & right")
        val someValue: Either</*errorMsg*/String,/*value*/String> = Either.right("Sample value")
        val result = someValue.swap()
        println("init value : $someValue \nafterSwap: $result ")

        println("# check if right have some value")
        val containsSample = someValue.contains("Sample value")
        println("init value : $someValue \ncontains?: $containsSample ")
    }

    fun defaultValueHandling() {
        println("# handling for default values")
        val someValue = "hello".left()
        val result1 = someValue.getOrElse { "world" }
        println("init value : $someValue \ndefaultValue: $result1 ")

        println("# handling for Error values")
        val result2 = someValue.getOrHandle { "world" }
        println("init value : $someValue \nerrorValue: $result2 ")

        println("# default values : ")
        12.right().getOrElse { 17 }.also(::println) // Result: 12
        12.left().getOrElse { 17 }.also(::println)  // Result: 17
        println("# Error values : ")
        12.right().getOrHandle { 17 }.also(::println) // Result: 12
        12.left().getOrHandle { it + 5 }.also(::println) // Result: 17
    }

    fun conditionalValue() {
        println("# Assign right value based on condition")
        val someValue1 = Either.conditionally(true/*apply value to right*/, { "Error" }, { 42 })
        val someValue2 = Either.conditionally(false/*apply value to left*/, { "Error" }, { 42 })
        println("$someValue1 : $someValue2")
    }
}


// Option<A> is a container for an optional value of type A
object OptionDemo {
    fun demo() {
        println("# --- ArrowKt-Option-type-examples ---")
        creation()
        defaultValue()
        fromNullableTypes()
        supportWhenStatement()
        operations()
    }

    fun creation() {
        println("# Some provide value to option , None defines empty value")
        val someValue: Option<String> = Some("I'm some value")
        val emptyValue: Option<String> = none()
        println("somevalue : $someValue \nemptyvalue : $emptyValue")
    }

    fun defaultValue() {
        println("# you can use getOrElse to provide default value for such operations")
        fun maybeEvenNumber(number: Int): Option<String> =
            if (number % 2 == 0) Some("Even") else None

        val result1 = maybeEvenNumber(2).getOrElse { "Nopes" }
        val result2 = maybeEvenNumber(5).getOrElse { "Nopes" }

        println("result1 : $result1 \nresult2 : $result2")
    }

    fun fromNullableTypes() {
        println("# support for nullable types")
        val myString: String? = "Nullable string"
        val optionFromNull: Option<String> = Option.fromNullable(myString)

        println("null -> option : $optionFromNull")
    }

    fun supportWhenStatement() {
        println("# example with `when` statement")
        fun maybeEvenNumber(number: Int): Option<String> =
            if (number % 2 == 0) Some("Even") else None

        val evenValue1: Option<String> = maybeEvenNumber(20)
        val value1 = when (evenValue1) {
            is Some -> evenValue1.t
            is None -> "Nopesss"
        }
        println("example 1 option with when: $value1")

        val evenValue2: Option<String> = maybeEvenNumber(13)
        val value2 = when (evenValue2) {
            is Some -> evenValue2.t
            is None -> "Nopesss"
        }
        println("example 2 option with when: $value2")
    }

    fun operations() {
        println("# Operation on optional type")
        val number: Option<Int> = Some(3)
        val noNumber: Option<Int> = None
        // operations on `Some` results in some transformation
        val mappedResult1 = number.map { it * 1.5 }
        // operations on `None` doesn't do anything
        val mappedResult2 = noNumber.map { it * 1.5 }

        println("map result on Option: $mappedResult1 : $mappedResult2")
    }
}