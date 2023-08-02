package mw.gclog

import com.microsoft.gctoolkit.GCToolKit
import com.microsoft.gctoolkit.io.GCLogFile
import com.microsoft.gctoolkit.io.SingleGCLogFile
import mw.gclog.aggregation.G1DataAggregation
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import kotlin.jvm.optionals.getOrDefault


private var initialMarkCount = 0
private var remarkCount = 0
private var defNewCount = 0

//@SpringBootApplication
class GcLogInterpreterApplication

fun main(args: Array<String>) {
    //val machine = GcLogAnalyzer()
    //machine.run()
    //runApplication<GcLogInterpreterApplication>(*args)
    val gcLogFile = "gclogs/2b3a8d4c-3d59-474e-b2a1-6030e4b7027d.log"

    require(!gcLogFile.isBlank()) { "This sample requires a path to a GC log file." }

    require(!Files.notExists(Path.of(gcLogFile))) { String.format("File %s not found.", gcLogFile) }

    analyze(gcLogFile)
}

@Throws(IOException::class)
fun analyze(gcLogFile: String?) {
    val start = Instant.now()

    /**
     * GC log files can come in  one of two types: single or series of rolling logs.
     * In this sample, we load a single log file.
     * The log files can be either in text, zip, or gzip format.
     */
    val logFile: GCLogFile = SingleGCLogFile(Path.of(gcLogFile))
    val gcToolKit = GCToolKit()
    /**
     * This call will load all implementations of Aggregator that have been declared in module-info.java.
     * This mechanism makes use of Module SPI.
     */
    /*gcToolKit.loadAggregationsFromServiceLoader()
    gcToolKit.loadAggregation(HeapOccupancyAfterCollectionSummary())
    gcToolKit.loadAggregation(PauseTimeSummary())
    gcToolKit.loadAggregation(CollectionCycleCountsSummary())*/

    gcToolKit.loadAggregation(G1DataAggregation())
    /**
     * The JavaVirtualMachine contains the aggregations as filled out by the Aggregators.
     * It also contains configuration information about how the JVM was configured for the runtime.
     */
    val machine = gcToolKit.analyze(logFile)

    // Retrieves the Aggregation for HeapOccupancyAfterCollectionSummary. This is a time-series aggregation.
    val message = ""
    machine.getAggregation(G1DataAggregation::class.java)
            .map { it.aggregations }
            .getOrDefault(emptyMap()).forEach { (gcType, dataSet) ->
                print { "The XYDataSet for $gcType contains $dataSet.size() items.\n" }
            }

    val df = machine.getAggregation(G1DataAggregation::class.java).get().dataFrame()
    df.writeCSV("gclogs/pauses.csv")

    val stop = Instant.now()

    val appDuration = Duration.between(start, stop).toMillis()

    println(" ====> Analysis finished after $appDuration ms <======")

}

