package tech.silky.asylum.client.std

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.registry.Registries
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.valueOf
import org.luaj.vm2.lib.TwoArgFunction
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import kotlin.random.Random

object AEntity {
    fun make(entity: Entity): LuaTable {
        val table = luaTable {
            attach("__entity", entity)
            metatable("__index", lib)
        }
        return table
    }

    val lib = luaTable {
        fn("get_type") { self ->
            val entity = self.inner<Entity>("__entity")
            val id = Registries.ENTITY_TYPE.getId(entity.type)
            return@fn AIdentifier.make(id.namespace, id.path)
        }
        fn("get_health") { self ->
            val entity = self.inner<Entity>("__entity")
            val hp = (entity as? LivingEntity)?.health ?: return@fn LuaValue.NIL
            return@fn valueOf(hp.toDouble())
        }
    }
}