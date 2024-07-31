package org.space.pmmp.process

import java.io.File

class Process private constructor(
    val pid: Int,
    val handle: java.lang.Process
) {

    companion object {
        fun create(command: List<String>, workingDirectory: String): Process {
            val handle: java.lang.Process = ProcessBuilder(command)
                .directory(File(workingDirectory))
                .start()

            val pid = getProcessId(handle)

            return Process(pid, handle)
        }

        private fun getProcessId(process: java.lang.Process): Int {
            val clazz = Class.forName("java.lang.UNIXProcess")
            val field = clazz.getField("pid")

            return field.get(process) as Int
        }
    }
}