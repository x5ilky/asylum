package tech.silky.asylum.client

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.LibFunction
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.ThreeArgFunction
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
    fun fn(name: String, body: (LuaValue, LuaValue, LuaValue) -> LuaValue) {
        table[name] = object : ThreeArgFunction() {
            override fun call(a: LuaValue, b: LuaValue, c: LuaValue): LuaValue = body(a, b, c)
        }
    }
    fun fn(name: String, body: (LuaValue, LuaValue, LuaValue, LuaValue) -> LuaValue) {
        table[name] = object : LibFunction() {
            override fun call(a: LuaValue, b: LuaValue, c: LuaValue, d: LuaValue): LuaValue = body(a, b, c, d)
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

    fun metatable(key: String, value: LuaValue) {
        val mt = metatable ?: LuaTable()
        mt[key] = value
        metatable = mt
    }

    fun build(): LuaTable {
        metatable?.let { table.setmetatable(it) }
        return table
    }
}

fun luaTable(init: LuaTableBuilder.() -> Unit): LuaTable =
    LuaTableBuilder().apply(init).build()
inline fun <reified T> LuaValue.inner(name: String): T = this.getmetatable().get(name).touserdata(T::class.java) as T
fun tryCall(v: () -> Unit) {
    try {
        v()
    } catch (e: LuaError) {
        println(e)
        val player = MinecraftClient.getInstance().player
        player?.sendMessage(Text.of("Error while running lua script"), false)
        for (ln in e.toString().lines()) {
            player?.sendMessage(Text.of(ln), false)
        }
    }
}