package tech.silky.asylum.client.std.player

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import org.luaj.vm2.LuaValue
import tech.silky.asylum.client.IOBJ
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.ATypes
import tech.silky.asylum.client.typecheck

object AInventory {
    fun make(inventory: Inventory): LuaValue {
        val table = luaTable {
            attach(IOBJ, inventory)
            value("__type", LuaValue.valueOf(ATypes.INVENTORY))

            metatable("__index", lib)
        }
        return table
    }

    val lib = luaTable {
        fn("size") { self ->
            typecheck { self with ATypes.INVENTORY }
            val inv = self.inner<Inventory>(IOBJ)
            return@fn LuaValue.valueOf(inv.size())
        }
        fn("get_item_stack_at") { self, slot ->
            typecheck { self with ATypes.INVENTORY; slot with ATypes.INTEGER }
            val inv = self.inner<Inventory>(IOBJ)
            return@fn AItemStack.make(inv.getStack(slot.toint()))
        }
        fn("is_player_inventory") { self ->
            typecheck { self with ATypes.INVENTORY }
            val inv = self.inner<Inventory>(IOBJ)
            return@fn LuaValue.valueOf(inv is PlayerInventory)
        }
        fn("get_selected_slot") { self ->
            typecheck { self with ATypes.INVENTORY }
            val inv = self.inner<Inventory>(IOBJ)
            if (inv is PlayerInventory) {
                return@fn LuaValue.valueOf((inv as PlayerInventory).selectedSlot)
            }
            return@fn LuaValue.NIL
        }
    }
}