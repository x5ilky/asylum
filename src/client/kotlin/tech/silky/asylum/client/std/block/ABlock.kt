package tech.silky.asylum.client.std.block

import net.minecraft.block.Block
import net.minecraft.registry.Registries
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.ZeroArgFunction
import tech.silky.asylum.client.std.AIdentifier

object ABlock {
    fun make(block: Block): LuaValue {
        val table = LuaTable()

        table.set("get_id", object : ZeroArgFunction() {
            override fun call(): LuaValue {
                val iden = Registries.BLOCK.getId(block)
                return AIdentifier.make(iden.namespace, iden.path)
            }
        })

        return table
    }
}