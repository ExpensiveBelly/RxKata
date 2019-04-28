import algorithm.LinkedListInput
import com.google.gson.Gson
import java.io.File

object Resources {

    fun fromJsonFile(fileName: String): Triple<List<Int>, List<Int>, List<Int>> {
        val readText = File(Resources.javaClass.classLoader.getResource(fileName).toURI()).readText()
        val fromJson = Gson().fromJson<LinkedListInput>(readText, LinkedListInput::class.java)

        return Triple(fromJson.input.a.toList(), fromJson.input.b.toList(), fromJson.output.toList())
    }
}