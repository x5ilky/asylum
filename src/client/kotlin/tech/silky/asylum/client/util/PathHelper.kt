package tech.silky.asylum.client.util

object PathHelper {
    fun getFolderFromPath(id: String): String {
        val index = id.lastIndexOf('/')
        return if (index != -1) id.substring(0, index) else ""
    }

    fun isValidIdentifier(id: String): Boolean {
        val regex = Regex("^([a-z0-9_.-]+:)?[a-z0-9_/.-]+$")

        val pathPart = id.substringAfter(':', id)
        if (pathPart.startsWith("/") || pathPart.endsWith("/")) return false
        if (pathPart.contains("//")) return false

        return regex.matches(id)
    }
}