@file:Suppress("PackageDirectoryMismatch")

package playground.kodein.db

import kotlinx.serialization.Serializable
import org.kodein.db.DB
import org.kodein.db.Key
import org.kodein.db.impl.open
import org.kodein.db.model.Id
import org.kodein.db.model.Indexed
import org.kodein.db.orm.kotlinx.KotlinxSerializer


/**
 * Kodein DB : painless embdeded NoSQL database
 *
- [GitHub](https://github.com/Kodein-Framework/Kodein-DB)
- [Official Website](https://docs.kodein.org/kodein-db/0.2.0/index.html)
 */
fun main() {

    val db = DB.open("users.db",
            KotlinxSerializer {
                +User.serializer()
                +Address.serializer()
            }
    )
    val keyBerlin = db.put(Address("uid-berlin", "Berlin"))
    val keyParis = db.put(Address("uid-paris", "Paris"))
    println(keyBerlin)
    println(keyParis)
}

@Serializable
internal data class User(
        @Id val uid: String,
        val firstName: String,
        val lastName: String,
        val address: Key<Address>
){
    @Indexed("name") fun nameIndex() =
            listOf(lastName, firstName)
}

@Serializable
internal data class Address(
        @Id val uid: String,
        val city: String
)