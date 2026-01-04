package tech.silky.asylum.client.std

import net.minecraft.util.Identifier
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.typecheck


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

    fun from(v: LuaValue): Identifier {
        return Identifier.of(v.get("namespace").toString(), v.get("path").toString())
    }

    val lib = luaTable {
        fn("make") { namespace, path ->
            typecheck {
                namespace with ATypes.STRING
                path with ATypes.STRING
            }
            return@fn make(namespace.toString(), path.toString())
        }
    }
}