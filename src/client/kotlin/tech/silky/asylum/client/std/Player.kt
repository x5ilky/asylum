package tech.silky.asylum.client.std

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.ZeroArgFunction
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.position.World

object Player {
    fun makePlayer(player: ClientPlayerEntity): LuaValue {
        val table = luaTable {
            fn("send_message") { arg ->
                player.sendMessage(Text.of(arg.checkjstring()), false)
                return@fn LuaValue.NIL
            }
            fn("get_display_name") { ->
                return@fn LuaValue.valueOf(player.name.toString())
            }
            fn("get_world") { ->
                return@fn World.make(player.entityWorld)
            }
        }


        return table
    }
}