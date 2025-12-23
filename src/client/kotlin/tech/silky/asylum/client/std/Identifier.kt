package tech.silky.asylum.client.std

import net.minecraft.util.Identifier
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction


object Identifier {
    fun make(identifier: Identifier): LuaValue {
        val table = LuaTable()

        table.set("get_namespace", object : ZeroArgFunction() {
            override fun call(): LuaValue {
                return valueOf(identifier.namespace)
            }
        })
        table.set("get_path", object : ZeroArgFunction() {
            override fun call(): LuaValue {
                return valueOf(identifier.path)
            }
        })

        val metatable = LuaTable()
        metatable.set("__tostring", object : TwoArgFunction() {
            override fun call(table: LuaValue, unused: LuaValue?): LuaValue? {
                return valueOf(identifier.toString())
            }
        })

        table.setmetatable(metatable)

        return table
    }
}