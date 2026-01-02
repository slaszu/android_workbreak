package pl.slaszu.localstorage

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.InputStream
import java.io.OutputStream

@OptIn(InternalSerializationApi::class)
inline fun <reified T> Context.createDataStore(default: T, filename: String?): DataStore<T> {

    return DataStoreFactory.create(
        serializer = object : Serializer<T> {
            override val defaultValue = default

            override suspend fun readFrom(input: InputStream): T {
                try {
                    return Json.decodeFromString<T>(
                        input.readBytes().decodeToString()
                    )
                } catch (serialization: SerializationException) {
                    Log.d(
                        "myapp",
                        serialization.message ?: "SerializationException but no message :/"
                    )
                    return defaultValue
                }
            }

            override suspend fun writeTo(t: T, output: OutputStream) {
                output.write(
                    Json.encodeToString(
                        t::class.serializer() as SerializationStrategy<T>,
                        t
                    ).encodeToByteArray()
                )
            }
        },
        produceFile = {
            val fileNamePath = when {
                filename != null -> filename
                else -> "${default::class}"
            }
            dataStoreFile("$fileNamePath.pb")
        }
    )
}

inline fun <reified T> Context.createLocalStorage(
    default: T,
    filename: String?,
): LocalStorage<T> {
    val dataStore = createDataStore(default, filename)
    return LocalStorage(dataStore)
}
