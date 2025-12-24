package tech.silky.asylum.client.std

import net.minecraft.util.Identifier
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction
import tech.silky.asylum.client.luaTable


object AIdentifier {
    fun make(namespace: String, path: String): LuaValue {
        val table = luaTable {
            value("namespace", LuaValue.valueOf(namespace))
            value("path", LuaValue.valueOf(path))
            metatable("__tostring", object : TwoArgFunction() {
                override fun call(table: LuaValue, unused: LuaValue?): LuaValue? {
                    return valueOf("${get("namespace")}:${get("path")}")
                }
            })
            metatable("__index", lib)
        }

        return table
    }

    val lib = luaTable {
        fn("make") { namespace, path ->
            return@fn make(namespace.toString(), path.toString())
        }
    }
}