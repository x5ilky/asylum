package tech.silky.asylum.client.std.player

import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.registry.Registries
import org.luaj.vm2.LuaValue
import tech.silky.asylum.client.IOBJ
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.AIdentifier
import tech.silky.asylum.client.std.ATypes
import tech.silky.asylum.client.std.block.ABlock
import tech.silky.asylum.client.std.text.ATextLib
import tech.silky.asylum.client.typecheck

object AItem {
    fun make(item: Item): LuaValue {
        val table = luaTable {
            attach(IOBJ, item)
            value("__type", LuaValue.valueOf(ATypes.ITEM))
            metatable("__index", lib)
        }
        return table
    }

    val lib = luaTable {
        fn("get_id") { self ->
            typecheck { self with ATypes.ITEM }
            val item = self.inner<Item>(IOBJ)

            val id: Identifier = Registries.ITEM.getId(item)
            return@fn AIdentifier.make(id.namespace, id.path)
        }
        fn("get_name") { self ->
            typecheck { self with ATypes.ITEM }
            val item = self.inner<Item>(IOBJ)

            return@fn ATextLib.make(item.name)
        }
        fn("get_max_count") { self ->
            typecheck { self with ATypes.ITEM }
            LuaValue.valueOf(self.inner<Item>(IOBJ).maxCount)
        }

        fn("is_block") { self ->
            typecheck { self with ATypes.ITEM }
            val item = self.inner<Item>(IOBJ)
            return@fn LuaValue.valueOf(item is BlockItem)
        }
        fn("get_block") { self ->
            typecheck { self with ATypes.ITEM }
            val item = self.inner<Item>(IOBJ)
            if (item !is BlockItem) return@fn LuaValue.NIL

            return@fn ABlock.make(item.block)
        }
    }
}
