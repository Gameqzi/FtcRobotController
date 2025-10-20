package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@TeleOp
class LL3AResultsIterative : OpMode() {

    // === EDIT THIS: set your LL's IP (static IP strongly recommended) ===
    private val llBase = "http://192.168.43.11:5807"

    private lateinit var fetcher: ResultsFetcher

    override fun init() {
        fetcher = ResultsFetcher("$llBase/results")
        fetcher.start()
        telemetry.addLine("Limelight fetcher starting…")
    }

    override fun start() {
        fetcher.running.set(true)
    }

    override fun loop() {
        // Grab the latest JSON snapshot (already parsed in the background)
        val json = fetcher.latest.get()
        if (json == null) {
            telemetry.addLine("Waiting for Limelight…")
        } else {
            // 1) Your Python SnapScript output (array you return as llpython)
            val py: JSONArray = json.optJSONArray("PythonOut") ?: JSONArray()

            // Example: interpret first 4 slots: [hasTarget, x, y, size]
            val hasTarget = py.optInt(0, 0) == 1
            val x = py.optDouble(1, 0.0)
            val y = py.optDouble(2, 0.0)
            val size = py.optDouble(3, 0.0)

            telemetry.addData("HasTarget", hasTarget)
            telemetry.addData("x,y,size", "%.1f, %.1f, %.2f", x, y, size)

            // 2) (optional) show first AprilTag ID if you’re also running that pipeline
            val fid = json.optJSONArray("Fiducial")
            val firstTagId = fid?.optJSONObject(0)?.optInt("fID")
            if (firstTagId != null) telemetry.addData("FirstTag", firstTagId)
        }
        telemetry.update()
    }

    override fun stop() {
        fetcher.running.set(false)
        fetcher.interrupt()
    }
}

/** Background thread that polls the Limelight /results endpoint ~30 Hz and caches the latest JSON. */
class ResultsFetcher(private val resultsUrl: String) : Thread("LLResultsFetcher") {
    val running = AtomicBoolean(false)
    val latest = AtomicReference<JSONObject?>(null)

    override fun run() {
        while (!isInterrupted) {
            if (running.get()) {
                try {
                    val url = URL(resultsUrl)
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 150
                        readTimeout = 150
                    }
                    conn.inputStream.use { stream ->
                        val text = BufferedReader(InputStreamReader(stream)).readText()
                        latest.set(JSONObject(text))
                    }
                    conn.disconnect()
                } catch (_: Exception) {
                    // Keep old sample; throttle on error
                }
            }
            try {
                sleep(33) // ~30 Hz
            } catch (_: InterruptedException) {
                break
            }
        }
    }
}