package danlew

import org.junit.Test
import java.util.*

class SourcesTest {

    private val sources = Sources()

    @Test
    fun should_load_data_from_network_if_memory_and_disk_are_empty() {
        sources.source.test().assertValue(Optional.of(Data("Server Response #1")))
    }

    @Test
    fun should_load_data_from_memory_once_the_data_is_loaded_from_network() {
        sources.source.test()

        sources.source.test().assertValue(Optional.of(Data("Server Response #1")))
    }

    @Test
    fun should_load_data_from_disk_even_when_memory_is_wiped() {
        sources.source.test()
        sources.source.test()

        sources.clearMemory()

        //Value comes from disk
        sources.source.test().assertValue(Optional.of(Data("Server Response #1")))
    }

    @Test
    fun should_hit_the_network_twice_if_memory_and_disk_are_wiped_and_another_subscriber_subscribes() {
        sources.source.test()

        sources.clearMemory()
        sources.clearDisk()

        sources.source.test().assertValue(Optional.of(Data("Server Response #2")))
    }
}