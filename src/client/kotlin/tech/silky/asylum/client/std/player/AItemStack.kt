package tech.silky.asylum.client.std.player

import net.minecraft.item.ItemStack
import org.luaj.vm2.LuaValue
import tech.silky.asylum.client.IOBJ
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.ATypes
import tech.silky.asylum.client.std.text.ATextLib
import tech.silky.asylum.client.typecheck

object AItemStack {
    fun make(itemStack: ItemStack): LuaValue {
        val table = luaTable {
            attach(IOBJ, itemStack)
            value("__type", LuaValue.valueOf(ATypes.ITEM_STACK))
            metatable("__index", lib)
        }
        return table
    }

    val lib = luaTable {
        fn("is_empty") { self ->
            typecheck { self with ATypes.ITEM_STACK }
            val itemStack = self.inner<ItemStack>(IOBJ)
            return@fn LuaValue.valueOf(itemStack.isEmpty)
        }
        fn("get_item") { self ->
            typecheck { self with ATypes.ITEM_STACK }
            val itemStack = self.inner<ItemStack>(IOBJ)
            return@fn AItem.make(itemStack.item)
        }
        fn("get_count") { self ->
            typecheck { self with ATypes.ITEM_STACK }
            val itemStack = self.inner<ItemStack>(IOBJ)
            return@fn LuaValue.valueOf(itemStack.count)
        }
        fn("get_max_count") { self ->
            typecheck { self with ATypes.ITEM_STACK }
            val itemStack = self.inner<ItemStack>(IOBJ)
            return@fn LuaValue.valueOf(itemStack.maxCount)
        }
        fn("get_name") { self ->
            typecheck { self with ATypes.ITEM_STACK }
            val itemStack = self.inner<ItemStack>(IOBJ)
            return@fn ATextLib.make(itemStack.name)
        }
    }
}