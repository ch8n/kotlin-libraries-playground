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
    /*wip*/ ValidateDemo.demo()
    println()
    NonEmptyListDemo.demo()
    println()
    ListKDemo.demo()
    println()
    MapKDemo.demo()
    println()
    EvalDemo.demo()

}

/***
 * Eval is a monad which controls evaluation of a value
 */
object EvalDemo {
    fun demo() {


    }

}

/***
 * MapK is an Arrow wrapper over Kotlin `Map type`.
 */
object MapKDemo {
    fun demo() {
        creation()
        operation()
    }

    private fun operation() {
        println("# Basic operations of mapK")
        val sampleMapK = mapOf("one" to 1, "two" to 2).k()
        val transformMapK: MapK<String, Int> = sampleMapK.map { entryValue -> entryValue.plus(10) }
        println("transformation using map : $sampleMapK to $transformMapK")

        val foldLeft: Int = sampleMapK.foldLeft(0) { acc, item ->
            item + acc
        }
        println("transformation using foldLeft : $sampleMapK to $foldLeft")

        val foldRight: Eval<String> = sampleMapK.foldRight(Eval.just("one")) { entry, eval ->
            Eval.just("$entry ${eval.value()}")
        }
        println("transformation using foldLefy : $sampleMapK to $foldRight")

    }

    fun creation() {
        println("# Creating a  mapK type")
        val mapK1: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()
        println(" map using extension `k()` : $mapK1")
        val mapK2: MapK<String, Int> = MapK(mapOf("one" to 1, "two" to 2))
        println(" map using constructor `MapK()` : $mapK2")
    }
}

/***
 * ListK wraps over the platform `List` datatype
 */
object ListKDemo {
    fun demo() {
        println("# --- ArrowKt-ListK-type-examples ---")
        creation()
        operation()
    }

    fun creation() {
        println("# Creating a  list")
        val platformList = listOf(1, 3, 4, 5, 8)
        val listwithKTX = platformList.k()
        val listArrow = ListK(platformList)
        println(" list using extension `k()` : $listwithKTX")
        println(" list using `ListK()` : $listArrow")
    }

    fun operation() {
        println("# basic listK operations")
        val platformList = listOf(1, 3, 4, 5, 8)
        val listwithKTX = platformList.k()
        val listArrow = ListK(platformList)
        val merged = listwithKTX.combineK(listArrow)
        println("merging list : $merged")
    }
}

/**
 * Data type of ordered lists that have at least one value
 */
object NonEmptyListDemo {
    fun demo() {
        println("# --- ArrowKt-NonEmptyList-type-examples ---")
        creation()
        operations()
    }

    fun creation() {
        println("# Creating a  non-empty list")
        val listOfItems = NonEmptyList.of(1, 3, 4, 6, 2)
        // val listEmpty = NonEmptyList.of() <== wont compile
        println(listOfItems)
    }

    fun operations() {
        println("# basic operations of non-empty list")
        val listOfItems = NonEmptyList.of(1, 3, 4, 6, 2)
        println("head of list : ${listOfItems.head}")

        val sumOfItems = listOfItems.foldLeft(0) { acc, item -> acc + item }
        println("sum by reducing the list : ${sumOfItems}")

        val twiced = listOfItems.map { item -> item * 2 }
        println("twised the item using mao on list : $twiced")

        val strangerThings = twiced.flatMap { twiceItem ->
            listOfItems.map { listItem ->
                listItem - twiceItem
            }
        }
        println("flatmap action $strangerThings")
    }
}


/**
 *  used for having all errors reported simultaneously.
 * **/
object ValidateDemo {
    fun demo() {
        creation()
        validatedSupportWhen()
        defaultValue()
        getAllErrors()
    }

    sealed class Errors {
        object OddNumberError : Errors()
        object NotIntegerError : Errors()
    }


    fun creation() {
        val resultError: Validated<Errors, Int> = Invalid(Errors.OddNumberError)
        print(resultError)

        val resultSuccess: Validated<Errors, Int> = Valid(2)
        print(resultSuccess)
    }

    fun validatedSupportWhen() {
        val resultSuccess: Validated<Errors, Int> = Valid(2)
        val output = when (resultSuccess) {
            is Validated.Valid -> resultSuccess.a
            is Validated.Invalid -> 0
        }
        println(output)
    }

    fun defaultValue() {
        val resultError: Validated<Errors, Int> = Invalid(Errors.OddNumberError)
        println(resultError.getOrElse { 0 })
    }

    fun getAllErrors() {
        println("# === list of errors ===")
        val resultError: Validated<Errors, Int> = Invalid(Errors.OddNumberError)
        val output = resultError.fold(
            /*accumlate errors*/ { Validated.Invalid(NonEmptyList(it, listOf())) },
            /*accumlate valid*/{ Valid(0) }
        )
        println(output)
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