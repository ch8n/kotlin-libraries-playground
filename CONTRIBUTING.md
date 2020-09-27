# Contributing #hacktoberfest


- Clone the repo
- Make a branch called "kodein-db"
- Create a file inside `kotlin-jvm/src/main/kotlin/playground`

```kotlin
@file:Suppress("PackageDirectoryMismatch")

package playground.YYY


/**
 * XXX/YYY - 

- [GitHub](https://github.com/XXX/YYY)
- [Official Website](https://XXX)
 */
fun main() {

}
```

- Call your new function from `kotlin-jvm/src/main/kotlin/playground/_main.kt`

```diff
fun main() {
    playground.okhttp.main()
    println()
+   playground.kodein.db.main()
+    println()
}
```

- 