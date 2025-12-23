package tech.silky.asylum.client.events

import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents
import tech.silky.asylum.client.std.Events
import tech.silky.asylum.client.std.Player
import tech.silky.asylum.client.std.block.Block
import tech.silky.asylum.client.std.block.LocalPosition

object ClientBlockBreakListener {
    fun init() {
        ClientPlayerBlockBreakEvents
            .AFTER.register { world, player, pos, state ->
            for ((_, v) in Events.events["block_break"]!!) {
                v.call(
                    Player.makePlayer(player),
                    LocalPosition
                        .make(pos.x, pos.y, pos.z),
                    Block.make(state.block)
                )
            }
        }
    }
}
