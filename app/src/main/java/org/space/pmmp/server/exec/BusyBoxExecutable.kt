package org.space.pmmp.server.exec

import android.content.Context
import android.widget.Toast
import org.space.pmmp.R
import java.io.File
import java.io.FileOutputStream

class BusyBoxExecutable {

    companion object {
        private var executablePath: String? = null

        fun installed(context: Context): Boolean {
            val outputDir = context.applicationInfo.dataDir

            executablePath = "$outputDir/busybox"

            val file = File(executablePath!!)

            if (file.exists() && file.canExecute()) {
                return true
            }

            return false
        }

        fun extract(context: Context) {
            val outputDir = context.applicationInfo.dataDir
            executablePath = "$outputDir/busybox"

            if (installed(context))
                return

            val file = File(executablePath!!)

            val asset = context.assets.open("bin/busybox")
            val outputStream = FileOutputStream(file)

            val chunk = ByteArray(8192)
            var got: Int;

            while ((asset.read(chunk).also { got = it }) > 0) {
                outputStream.write(chunk, 0, got);
            }

            asset.close()
            outputStream.close()

            makeExecutable()
        }

        fun executablePath(): String {
            return executablePath!!
        }

        fun makeExecutable(): Boolean {
            val file = File(executablePath())

            file.setExecutable(true)

            return file.canExecute()
        }
    }

}