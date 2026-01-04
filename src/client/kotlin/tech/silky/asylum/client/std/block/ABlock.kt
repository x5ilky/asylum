package tech.silky.asylum.client.std.block

import net.minecraft.block.Block
import net.minecraft.registry.Registries
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.ZeroArgFunction
import tech.silky.asylum.client.IOBJ
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.AIdentifier
import tech.silky.asylum.client.std.ATypes
import tech.silky.asylum.client.typecheck

object ABlock {
    fun make(block: Block): LuaValue {
        val table = luaTable {
            value("__type", LuaValue.valueOf(ATypes.BLOCK))
            attach(IOBJ, block)

            fn("get_id") { block ->
                typecheck {
                    block with ATypes.BLOCK
                }
                val block = block.inner<Block>(IOBJ)
                val iden = Registries.BLOCK.getId(block)
                return@fn AIdentifier.make(iden.namespace, iden.path)
            }
        }

        return table
    }
}