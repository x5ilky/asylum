package tech.silky.asylum.client.events

import tech.silky.asylum.client.event.ClientPlayerHitEntityEvent
import tech.silky.asylum.client.std.AEvents
import tech.silky.asylum.client.std.AClientPlayer
import tech.silky.asylum.client.std.AEntity
import tech.silky.asylum.client.std.AEventType
import tech.silky.asylum.client.tryCall

object ClientDamageEntityListener {
    fun init() {
        ClientPlayerHitEntityEvent
            .EVENT.register { player, entity ->
            for ((_, v) in AEvents.events[AEventType.PLAYER_DAMAGE_ENTITY]!!) {
                tryCall {
                    v.call(
                        AClientPlayer.makePlayer(player),
                        AEntity.make(entity)
                    )
                }
            }
        }
    }
}
