package tech.silky.asylum.client.std.block

import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.ZeroArgFunction
import tech.silky.asylum.client.std.Identifier

object Block {
    fun make(block: Block): LuaValue {
        val table = LuaTable()

        table.set("get_id", object : ZeroArgFunction() {
            override fun call(): LuaValue {
                return Identifier.make(Registries.BLOCK.getId(block))
            }
        })

        return table
    }
}