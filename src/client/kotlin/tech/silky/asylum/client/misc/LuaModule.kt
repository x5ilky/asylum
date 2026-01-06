package tech.silky.asylum.client.misc

import org.luaj.vm2.LuaValue

data class LuaModule(
    val id: String,
    val dependencies: List<String>,

    val init: LuaValue
) {

}