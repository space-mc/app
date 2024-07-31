package org.space.pmmp.server

class PocketMineServer(private val serverManager: ServerManager) {

    /*
    var console: ServerConsole = ServerConsole(this)

    var running: MutableState<Boolean> = mutableStateOf(false)
    var stopping: Boolean = false

    var process: Process? = null

    private var waitingThread: Thread? = null

    fun start(context: Context, path: String): Boolean {
        if (running.value) {
            throw IllegalStateException("The server is already running")
        }

        val directory = File(path)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val iniFile = File(pathFromString(path, "php.ini"))
        val pocketMineFile = File(pathFromString(path, "PocketMine-MP.phar"))

        Log.i("PMMP", "Starting server process at $path")

        if (!iniFile.exists()) {
            iniFile.createNewFile()
        }

        val command = listOf(
            PHPExecutable.executablePath(),
            "-c",
            iniFile.absolutePath,
            pocketMineFile.absolutePath
        )

        Log.i("RunPocketMine", "command: $command")

        process = ProcessBuilder()
            .directory(directory)
            .command(command)
            .start()

        console.start()

        waitingThread = Thread {
            process!!.waitFor()
            console.stop()

            serverManager.stopNotification(context)

            running.value = false
        }

        waitingThread!!.start()

        running.value = true

        return true
    }

    fun stop(killProcess: Boolean): Boolean {
        if (!running.value) {
            throw IllegalStateException("The server is not running")
        }

        if (!killProcess && !stopping) {
            console.inputLine("stop")

            stopping = true

            return true
        }

        running.value = false

        console.stop()

        if (process == null)
            return false

        process!!.destroy()
        process = null

        return true
    }
    
     */
}

