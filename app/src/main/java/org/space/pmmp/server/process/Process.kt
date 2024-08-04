package org.space.pmmp.server.process

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import java.io.File
import java.util.concurrent.TimeUnit

class Process private constructor(
    val pid: Int,
    val handle: java.lang.Process,
    val onStart: (Process) -> Unit,
    val onStop: (Process) -> Unit
) {

    val processState: MutableState<ProcessState> = mutableStateOf(ProcessState.STOPPED)

    // These values are updated in a thread monitoring the process state
    val cpuUsage: MutableState<Long> = mutableLongStateOf(-1L)
    val memoryUsage: MutableState<Long> = mutableLongStateOf(-1)
    val threadCount: MutableState<Int> = mutableIntStateOf(-1)

    // The thread that analyzes the process
    private val processMonitorTask: Thread = Thread {
        var lastCpuTime: Long = cpuTime()
        var lastTime: Long = System.currentTimeMillis()

        processState.value = ProcessState.STARTED

        onStart(this)

        while (true) {
            val currentTime = System.currentTimeMillis()
            val currentCpuTime = cpuTime()

            val timeDiff = (currentTime - lastTime).toFloat()
            val cpuTimeDiff = (currentCpuTime - lastCpuTime).toFloat()

            cpuUsage.value = (
                    if (timeDiff != 0f)
                        (cpuTimeDiff / timeDiff) * 100
                    else
                        0f
                    ).toLong()

            memoryUsage.value = memoryUsage()

            threadCount.value = threadCount()

            lastTime = currentTime
            lastCpuTime = currentCpuTime

            // Sleep for 2 seconds to update the values again
            if (handle.waitFor(2, TimeUnit.SECONDS)) {
                break
            }
        }

        processState.value = ProcessState.STOPPED
        onStop(this)
    }

    companion object {
        fun create(
            command: List<String>,
            workingDirectory: String,
            onStart: (Process) -> Unit,
            onStop: (Process) -> Unit
        ): Process {
            val handle: java.lang.Process = ProcessBuilder(command)
                .directory(File(workingDirectory))
                .start()

            val pid = getProcessId(handle)

            return Process(pid, handle, onStart, onStop)
        }

        @SuppressLint("DiscouragedPrivateApi")
        private fun getProcessId(process: java.lang.Process): Int {
            val clazz = Class.forName("java.lang.UNIXProcess")
            val field = clazz.getDeclaredField("pid")

            field.isAccessible = true

            return field.get(process) as Int
        }
    }

    init {
        processMonitorTask.start()
    }

    private external fun cpuTime(): Long

    private external fun memoryUsage(): Long

    private external fun threadCount(): Int

}