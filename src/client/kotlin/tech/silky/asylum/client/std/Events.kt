package tech.silky.asylum.client.std

import net.minecraft.text.Text
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.NIL
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.ZeroArgFunction

object Events {
    var eventCount = 1000

    var events = mutableMapOf<String, MutableMap<Int, LuaValue>>()
    init {
        initEvents()
    }

    private fun initEvents() {
        events["block_break"] = mutableMapOf()
    }

    fun makeObject(): LuaTable {
        val table = LuaTable()

        table.set("on_block_break", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val id = eventCount++
                events["block_break"]!![id] = arg
                return NIL
            }
        })
        table.set("remove_event", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                for ((_, v) in events) {
                    v.remove(arg.toint())
                }
                return NIL
            }
        })

        return table
    }
}