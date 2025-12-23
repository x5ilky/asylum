package tech.silky.asylum.client.std

import net.minecraft.client.MinecraftClient
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaNil
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ZeroArgFunction

object MinecraftLib : TwoArgFunction() {
    lateinit var globals: Globals
    override fun call(modname: LuaValue, env: LuaValue): LuaValue {
        globals = env.checkglobals();
        val mc = LuaTable()
        mc.set("get_player", GetPlayer);
        mc.set("events", Events.makeObject());
        env.set("mc", mc);
        env.get("package").get("loaded").set("mc", mc);
        return mc;
    }

    object GetPlayer : ZeroArgFunction() {
        override fun call(): LuaValue {
            val player = MinecraftClient.getInstance().player ?: return LuaNil.NIL
            return Player.makePlayer(player)
        }
    }
}