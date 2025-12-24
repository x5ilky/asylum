package tech.silky.asylum.client.std.position

import net.minecraft.world.World
import org.luaj.vm2.LuaValue
import tech.silky.asylum.client.luaTable

object AWorld {
    fun make(world: World): LuaValue {
        val table = luaTable {
            attach("__world", world)
        }
        return table
    }
    fun retrieve(value: LuaValue): World? {
        return value.getmetatable().get("__world").touserdata(World::class.java) as? World
    }
}