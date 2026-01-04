package tech.silky.asylum.client.std

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.NIL
import org.luaj.vm2.LuaValue.valueOf
import org.luaj.vm2.lib.TwoArgFunction
import tech.silky.asylum.client.IOBJ
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.position.AWorld
import tech.silky.asylum.client.typecheck
import kotlin.random.Random

object AClientPlayer {
    fun makePlayer(player: ClientPlayerEntity): LuaValue {
        val table = luaTable {
            value("__type", LuaValue.valueOf(ATypes.CLIENT_PLAYER))
            attach(IOBJ, player)
            value("_id", LuaValue.valueOf(Random.nextInt(9999)))
            metatable("__index", lib)
            metatable("__tostring", object : TwoArgFunction() {
                override fun call(table: LuaValue, unused: LuaValue?): LuaValue? {
                    return valueOf("")
                }
            })
        }

        return table
    }
    val lib = luaTable {
        fn("get_instance") { ->
            val player = MinecraftClient.getInstance().player ?: return@fn NIL
            return@fn makePlayer(player)
        }
        fn("send_message") { self, arg ->
            typecheck { self with ATypes.CLIENT_PLAYER; arg with ATypes.TEXT }
            val player = self.inner<ClientPlayerEntity>(IOBJ)
            val text = arg.inner<Text>(IOBJ)
            player.sendMessage(text, false)
            return@fn LuaValue.NIL
        }

        fn("get_display_name") { self ->
            typecheck { self with ATypes.CLIENT_PLAYER }
            val player = self.inner<ClientPlayerEntity>(IOBJ)
            return@fn valueOf(player.name.literalString)
        }
        fn("get_world") { self ->
            typecheck { self with ATypes.CLIENT_PLAYER }
            val player = self.inner<ClientPlayerEntity>(IOBJ)
            return@fn AWorld.make(player.entityWorld)
        }
        fn("play_sound") { self, soundId, volume, pitch ->
            typecheck {
                self with ATypes.CLIENT_PLAYER
                soundId with ATypes.IDENTIFIER
                volume with ATypes.DOUBLE
                pitch with ATypes.DOUBLE
            }
            val player = self.inner<ClientPlayerEntity>(IOBJ)
            val soundIdentifier = Identifier.of(
                soundId.get("namespace").toString(),
                soundId.get("path").toString(),
            )

            player.playSound(SoundEvent.of(soundIdentifier), volume.tofloat(), pitch.tofloat())

            return@fn LuaValue.NIL
        }
    }
}