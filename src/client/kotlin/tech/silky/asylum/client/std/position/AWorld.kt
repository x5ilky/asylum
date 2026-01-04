package tech.silky.asylum.client.std.position

import net.minecraft.world.World
import org.luaj.vm2.LuaValue
import tech.silky.asylum.client.IOBJ
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.ATypes

object AWorld {
    fun make(world: World): LuaValue {
        val table = luaTable {
            value("__type", LuaValue.valueOf(ATypes.WORLD))
            attach(IOBJ, world)
        }
        return table
    }
    fun retrieve(value: LuaValue): World? {
        return value.getmetatable().get(IOBJ).touserdata(World::class.java) as? World
    }
}