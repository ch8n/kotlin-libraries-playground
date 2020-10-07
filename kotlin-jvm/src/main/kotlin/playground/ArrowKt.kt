@file:Suppress("PackageDirectoryMismatch")

package playground.arrowkt

import arrow.core.*
import arrow.core.extensions.option.monad.map
import arrow.core.extensions.option.semigroupal.times

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

}

/**
 * Wrapper for writing functions that can fail
 * Either is used to short-circuit a computation upon the first error
 * Either is right-biased, i.e right contains the value and left contains error
 */

object EitherDemo {
    fun demo() {
        println("# --- ArrowKt-Either-type-examples ---")
        creation()
    }

    fun creation() {
        println("# Either creation sample")
        val someValue: Either</*errorMsg*/String,/*value*/String> = Either.right("Sample value")
        val newValue = someValue.flatMap {
            val mutation =StringBuilder().append(it).append("***").append(it).toString()
            Either.right(mutation)
        }
        println("initValue : $someValue \nAfter transformation: $newValue")
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