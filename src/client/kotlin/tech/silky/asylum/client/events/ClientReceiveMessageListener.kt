package tech.silky.asylum.client.events

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import org.luaj.vm2.LuaValue
import tech.silky.asylum.client.std.player.AClientPlayer
import tech.silky.asylum.client.std.AEntity
import tech.silky.asylum.client.std.AEventType
import tech.silky.asylum.client.std.AEvents
import tech.silky.asylum.client.std.text.ATextLib
import tech.silky.asylum.client.tryCall
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

object ClientReceiveMessageListener {
    fun init() {
        ClientReceiveMessageEvents.CHAT.register { text, message, profile, parameters, instant ->
            for ((_, v) in AEvents.events[AEventType.MESSAGE_RECEIVED]!!) {
                tryCall {
                    v.call(
                        ATextLib.make(text)
                    )
                }
            }
        }
    }
}