package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import java.io.File

class MakeTXT : OpMode() {

    private val fileName = "number.json"
    private var savedNumber: Int = 0

    override fun init() {
        saveNumber(21)
    }

    override fun loop() {
    }

    private fun saveNumber(num: Int) {
        val file = File(hardwareMap.appContext.filesDir, fileName)
        file.writeText(num.toString())
    }
}
