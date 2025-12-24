package tech.silky.asylum.client.std

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction

object AEvents {
    var eventCount = 1000

    var events = mutableMapOf<String, MutableMap<Int, LuaValue>>()
    init {
        initEvents()
    }

    private fun initEvents() {
        events["block_break"] = mutableMapOf()
        events["player_damage_entity"] = mutableMapOf()
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
        table.set("on_player_damage_entity", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                val id = eventCount++
                events["player_damage_entity"]!![id] = arg
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