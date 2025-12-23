package tech.silky.asylum.client.std.block

import net.minecraft.util.math.BlockPos
import org.luaj.vm2.Lua
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.position.World

object LocalPosition {
    fun make(x: Int, y: Int, z: Int): LuaValue {
        val table = luaTable {
            fn("get_block") { rawWorld ->
                val world = World.retrieve(rawWorld) ?: return@fn LuaValue.NIL
                return@fn Block.make(world.getBlockState(
                    BlockPos(get("x").toint(), get("y").toint(), get("z").toint())
                ).block)
            }

            value("x", LuaValue.valueOf(x))
            value("y", LuaValue.valueOf(y))
            value("z", LuaValue.valueOf(z))
        }

        return table
    }
}