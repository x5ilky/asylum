package tech.silky.asylum.client

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua

class LuaTableBuilder {
    private val table = LuaTable()
    private var metatable: LuaTable? = null

    fun fn(name: String, body: () -> LuaValue) {
        table[name] = object : ZeroArgFunction() {
            override fun call(): LuaValue = body()
        }
    }

    fun fn(name: String, body: (LuaValue) -> LuaValue) {
        table[name] = object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue = body(arg)
        }
    }

    fun fn(name: String, body: (LuaValue, LuaValue) -> LuaValue) {
        table[name] = object : TwoArgFunction() {
            override fun call(a: LuaValue, b: LuaValue): LuaValue = body(a, b)
        }
    }

    fun value(name: String, v: LuaValue) {
        table[name] = v
    }
    fun get(name: String): LuaValue {
        return table.get(name)
    }

    fun attach(key: String, value: Any) {
        val mt = metatable ?: LuaTable().also {
            // lock metatable from Lua
            it["__metatable"] = LuaValue.FALSE
            metatable = it
        }

        mt[key] = CoerceJavaToLua.coerce(value)
    }

    fun build(): LuaTable {
        metatable?.let { table.setmetatable(it) }
        return table
    }
}

fun luaTable(init: LuaTableBuilder.() -> Unit): LuaTable =
    LuaTableBuilder().apply(init).build()