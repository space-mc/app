package org.space.pmmp.helper

fun pathFromString(vararg parts: String): String {
    val finalPath = StringBuilder()

    parts.forEachIndexed { index, part ->
        finalPath.append(part)

        if ((parts.size - 1) != index) {
            finalPath.append('/')
        }
    }

    return finalPath.toString()
}