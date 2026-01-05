package tech.silky.asylum.client

import net.minecraft.util.Identifier
import org.luaj.vm2.LuaValue

data class LuaModule(
    val id: String,
    val dependencies: List<String>,

    val init: LuaValue
) {

}
