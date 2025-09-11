package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import java.io.File

class PullTXT : OpMode() {

    private val fileName = "number.json"
    private var savedNumber: Int = 0

    override fun init() {
        savedNumber = readNumber()
    }

    override fun loop() {

    }

    private fun readNumber(): Int {
        val file = File(hardwareMap.appContext.filesDir, fileName)
        return if (file.exists()) {
            file.readText().toIntOrNull() ?: 0
        } else {
            0
        }
    }
}