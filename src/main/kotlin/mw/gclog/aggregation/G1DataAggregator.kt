package mw.gclog.aggregation

import com.microsoft.gctoolkit.aggregator.*
import com.microsoft.gctoolkit.event.GarbageCollectionTypes
import com.microsoft.gctoolkit.event.g1gc.G1GCConcurrentEvent
import com.microsoft.gctoolkit.event.g1gc.G1GCEvent
import com.microsoft.gctoolkit.event.g1gc.G1GCPauseEvent
import org.jetbrains.kotlinx.dataframe.api.toDataFrame

@Aggregates(EventSource.G1GC)
class G1DataAggregator(results: G1DataAggregation) :
        Aggregator<G1DataAggregation>(results) {
    init {
        register(G1GCPauseEvent::class.java) { event: G1GCPauseEvent -> this.extractHeapOccupancy(event) }
        register(G1GCConcurrentEvent::class.java) { event: G1GCConcurrentEvent -> this.extractHeapOccupancy(event) }
    }

    private fun extractHeapOccupancy(event: G1GCEvent) {
        aggregation().addDataPoint(event.garbageCollectionType, event)
    }
}

@Collates(G1DataAggregator::class)
class G1DataAggregation : Aggregation() {

    val aggregations: MutableMap<GarbageCollectionTypes, G1DataPoints> = mutableMapOf()
    val eventDataFrame: Map<String, MutableList<Any>> = initialDataFrame()

    fun addDataPoint(gcType: GarbageCollectionTypes, event: G1GCEvent) {
        aggregations.getOrPut(gcType) { G1DataPoints() }.add(event)
        when (event) {
            is G1GCConcurrentEvent -> toDataFrame(event)
            is G1GCPauseEvent -> toDataFrame(event)
        }
    }

    override fun hasWarning(): Boolean {
        return false
    }

    override fun isEmpty(): Boolean {
        return false
    }

    private fun initialDataFrame() = mapOf<String, MutableList<Any>>(
            "type" to mutableListOf(),
            "event_type" to mutableListOf(),
            "startedAt" to mutableListOf(),
            "duration" to mutableListOf(),
            "cause" to mutableListOf(),
            "cpu_user" to mutableListOf(),
            "cpu_kernel" to mutableListOf(),
            "cpu_wallClock" to mutableListOf(),
            "heap_occupancy_before_coll" to mutableListOf(),
            "heap_occupancy_after_coll" to mutableListOf(),
            "heap_size_before_coll" to mutableListOf(),
            "heap_size_after_coll" to mutableListOf(),
            "perm_or_meta_occupancy_before_coll" to mutableListOf(),
            "perm_or_meta_occupancy_after_coll" to mutableListOf(),
            "perm_or_meta_size_before_coll" to mutableListOf(),
            "perm_or_meta_size_after_coll" to mutableListOf()
    )


    fun dataFrame() = eventDataFrame.toDataFrame()


    private fun toDataFrame(event: G1GCPauseEvent) {
        eventDataFrame.forEach { (k, v) ->
            when (k) {
                "type" -> v.add(event.garbageCollectionType)
                "event_type" -> v.add("PauseEvent")
                "startedAt" -> v.add(event.dateTimeStamp.dateTime)
                "duration" -> v.add(event.duration)
                "cause" -> v.add(event.gcCause)
                "cpu_user" -> v.add(event.cpuSummary?.user ?: 0)
                "cpu_kernel" -> v.add(event.cpuSummary?.kernel ?: 0)
                "cpu_wallClock" -> v.add(event.cpuSummary?.wallClock ?: 0)
                "heap_occupancy_before_coll" -> v.add(event.heap?.occupancyBeforeCollection ?: 0)
                "heap_occupancy_after_coll" -> v.add(event.heap?.occupancyAfterCollection ?: 0)
                "heap_size_before_coll" -> v.add(event.heap?.sizeBeforeCollection ?: 0)
                "heap_size_after_coll" -> v.add(event.heap?.sizeAfterCollection ?: 0)
                "perm_or_meta_occupancy_before_coll" -> v.add(event.permOrMetaspace?.occupancyBeforeCollection ?: 0)
                "perm_or_meta_occupancy_after_coll" -> v.add(event.permOrMetaspace?.occupancyAfterCollection ?: 0)
                "perm_or_meta_size_before_coll" -> v.add(event.permOrMetaspace?.sizeBeforeCollection ?: 0)
                "perm_or_meta_size_after_coll" -> v.add(event.permOrMetaspace?.sizeAfterCollection ?: 0)
            }

        }
    }

    private fun toDataFrame(event: G1GCConcurrentEvent) {
        eventDataFrame.forEach { (k, v) ->
            when (k) {
                "type" -> v.add(event.garbageCollectionType)
                "event_type" -> v.add("ConcurrentEvent")
                "startedAt" -> v.add(event.dateTimeStamp.dateTime)
                "duration" -> v.add(event.duration)
                "cause" -> v.add(event.gcCause)
                "cpu_user" -> v.add(0)
                "cpu_kernel" -> v.add(0)
                "cpu_wallClock" -> v.add(0)
                "heap_occupancy_before_coll" -> v.add(0)
                "heap_occupancy_after_coll" -> v.add(0)
                "heap_size_before_coll" -> v.add(0)
                "heap_size_after_coll" -> v.add(0)
                "perm_or_meta_occupancy_before_coll" -> v.add(0)
                "perm_or_meta_occupancy_after_coll" -> v.add(0)
                "perm_or_meta_size_before_coll" -> v.add(0)
                "perm_or_meta_size_after_coll" -> v.add(0)
            }

        }
    }

}


data class G1DataPoints(val dataSeries: MutableList<G1GCEvent> = mutableListOf()) {
    fun add(point: G1GCEvent) = dataSeries.add(point)
    fun size() = dataSeries.size
}


