package tech.silky.asylum.client.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity

fun interface ClientPlayerHitEntityEvent {

    fun onHit(player: ClientPlayerEntity, entity: Entity)

    companion object {
        val EVENT: Event<ClientPlayerHitEntityEvent> =
            EventFactory.createArrayBacked(ClientPlayerHitEntityEvent::class.java) { listeners ->
                ClientPlayerHitEntityEvent { player, entity ->
                    for (listener in listeners) {
                        listener.onHit(player, entity)
                    }
                }
            }
    }
}
