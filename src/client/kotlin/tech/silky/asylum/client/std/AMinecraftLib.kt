package tech.silky.asylum.client.std

import net.minecraft.client.MinecraftClient
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction
import tech.silky.asylum.client.std.hud.AHudLib

object AMinecraftLib : TwoArgFunction() {
    lateinit var globals: Globals
    override fun call(modname: LuaValue, env: LuaValue): LuaValue {
        globals = env.checkglobals()
        val mc = LuaTable()
        mc.set("get_player", GetPlayer)
        mc.set("events", AEvents.makeObject())
        mc.set("identifier", AIdentifier.lib)
        mc.set("client_player", AClientPlayer.lib)
        mc.set("hud", AHudLib.lib)

        env.set("mc", mc)
        env.get("package").get("loaded").set("mc", mc)
        return mc
    }

    object GetPlayer : ZeroArgFunction() {
        override fun call(): LuaValue {
            val player = MinecraftClient.getInstance().player ?: return NIL
            return AClientPlayer.makePlayer(player)
        }
    }
}