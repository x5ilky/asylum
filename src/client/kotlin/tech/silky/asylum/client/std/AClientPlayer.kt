package tech.silky.asylum.client.std

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.valueOf
import org.luaj.vm2.lib.TwoArgFunction
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.position.AWorld
import kotlin.random.Random

object AClientPlayer {
    fun makePlayer(player: ClientPlayerEntity): LuaValue {
        val table = luaTable {
            attach("__player", player)
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
        fn("send_message") { self, arg ->
            val player = self.inner<ClientPlayerEntity>("__player")
            player.sendMessage(Text.of(arg.checkjstring()), false)
            return@fn LuaValue.NIL
        }
        fn("get_display_name") { self ->
            val player = self.inner<ClientPlayerEntity>("__player")
            return@fn valueOf(player.name.literalString)
        }
        fn("get_world") { self ->
            val player = self.inner<ClientPlayerEntity>("__player")
            return@fn AWorld.make(player.entityWorld)
        }
        fn("play_sound") { self, soundId, volume, pitch ->
            val player = self.inner<ClientPlayerEntity>("__player")
            val soundIdentifier = Identifier.of(
                soundId.get("namespace").toString(),
                soundId.get("path").toString(),
            )

            player.playSound(SoundEvent.of(soundIdentifier), volume.tofloat(), pitch.tofloat())

            return@fn LuaValue.NIL
        }
    }
}