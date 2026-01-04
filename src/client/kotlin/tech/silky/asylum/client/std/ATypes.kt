package tech.silky.asylum.client.std

import tech.silky.asylum.client.tableOf

object ATypes {
    const val STRING = "string"
    const val INTEGER = "integer"
    const val BOOLEAN = "boolean"
    const val DOUBLE = "double"
    const val NIL = "nil"
    const val TABLE = "table"
    const val FUNCTION = "function"

    const val BLOCK = "std.block"
    const val DRAW_CONTEXT = "std.hud.draw_context"
    const val LOCAL_POSITION = "std.position.local_position"
    const val WORLD = "std.position.world"
    const val TEXT = "std.text.text"
    const val CLIENT_PLAYER = "std.client_player"
    const val ENTITY = "std.entity"

    val IDENTIFIER = tableOf("std.identifier") {
        "namespace" with STRING
        "path" with STRING
    }

    val TEXT_STYLE = tableOf("std.text.style") {
        "bold" with BOOLEAN
        "italic" with BOOLEAN
        "underline" with BOOLEAN
        "obfuscated" with BOOLEAN
        "color" with INTEGER
    }
}