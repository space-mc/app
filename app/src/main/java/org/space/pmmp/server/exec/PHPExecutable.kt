package org.space.pmmp.server.exec

import android.content.Context
import java.io.File
import java.io.FileOutputStream

class PHPExecutable {

    companion object {
        private var executablePath: String? = null

        fun installed(context: Context): Boolean {
            val outputDir = context.applicationInfo.dataDir

            executablePath = "$outputDir/php"

            val file = File(executablePath!!)

            if (file.exists() && file.canExecute()) {
                return true
            }

            return false
        }

        fun extract(context: Context) {
            val outputDir = context.applicationInfo.dataDir
            executablePath = "$outputDir/php"

            if (installed(context))
                return

            val file = File(executablePath!!)

            val asset = context.assets.open("bin/php")
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