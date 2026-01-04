package tech.silky.asylum.client.std

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import tech.silky.asylum.client.typecheck

object AEventType {
    const val BLOCK_BREAK = "block_break"
    const val PLAYER_DAMAGE_ENTITY = "player_damage_entity"
    const val DISABLE = "disable"
    const val MESSAGE_RECEIVED = "message_received"

    val ALL_EVENTS = arrayOf(
        BLOCK_BREAK, PLAYER_DAMAGE_ENTITY, DISABLE, MESSAGE_RECEIVED
    )
}

object AEvents {
    var eventCount = 1000

    var events = mutableMapOf<String, MutableMap<Int, LuaValue>>()
    init {
        initEvents()
    }

    private fun initEvents() {
        for (event in AEventType.ALL_EVENTS)
            events[event] = mutableMapOf()
    }

    fun makeObject(): LuaTable {
        val table = LuaTable()

        for (event in AEventType.ALL_EVENTS) {
            table.set("on_$event", object : OneArgFunction() {
                override fun call(arg: LuaValue): LuaValue {
                    typecheck {
                        arg with ATypes.FUNCTION
                    }
                    val id = eventCount++
                    events[event]!![id] = arg
                    return NIL
                }
            })
        }
        return table
    }
}