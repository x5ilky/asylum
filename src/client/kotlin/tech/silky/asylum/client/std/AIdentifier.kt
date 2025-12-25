package tech.silky.asylum.client.std

import net.minecraft.util.Identifier
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction
import tech.silky.asylum.client.luaTable


object AIdentifier {
    fun make(namespace: String, path: String): LuaValue {
        val table = luaTable {
            value("__type", LuaValue.valueOf(this@AIdentifier.hashCode()))
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
        if (v.get("__type").toint() != this@AIdentifier.hashCode()) {
            throw LuaError("Object passed in is not an AIdentifier")
        }
        return Identifier.of(v.get("namespace").toString(), v.get("path").toString())
    }

    val lib = luaTable {
        fn("make") { namespace, path ->
            return@fn make(namespace.toString(), path.toString())
        }
    }
}