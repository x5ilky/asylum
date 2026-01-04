package tech.silky.asylum.client.events

import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents
import tech.silky.asylum.client.std.AEvents
import tech.silky.asylum.client.std.AClientPlayer
import tech.silky.asylum.client.std.AEventType
import tech.silky.asylum.client.std.block.ABlock
import tech.silky.asylum.client.std.position.ALocalPosition
import tech.silky.asylum.client.tryCall

object ClientBlockBreakListener {
    fun init() {
        ClientPlayerBlockBreakEvents
            .AFTER.register { world, player, pos, state ->
            for ((_, v) in AEvents.events[AEventType.BLOCK_BREAK]!!) {
                tryCall {
                    v.call(
                        AClientPlayer.makePlayer(player),
                        ALocalPosition
                            .make(pos.x, pos.y, pos.z),
                        ABlock.make(state.block)
                    )
                }
            }
        }
    }
}
