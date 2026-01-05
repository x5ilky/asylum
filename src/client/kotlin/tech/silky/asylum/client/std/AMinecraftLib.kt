package tech.silky.asylum.client.std

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.TwoArgFunction
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.hud.AHudLib
import tech.silky.asylum.client.std.text.ATextLib

object AMinecraftLib : TwoArgFunction() {
    lateinit var globals: Globals
    override fun call(modname: LuaValue, env: LuaValue): LuaValue {
        globals = env.checkglobals()
        val mc = luaTable {
            value("events", AEvents.makeObject())
            value("identifier", AIdentifier.lib)
            value("client_player", AClientPlayer.lib)
            value("hud", AHudLib.lib)
            value("events", AEvents.makeObject())
            value("text", ATextLib.lib)
            value("modules", luaTable {})
            value("module_path", luaTable {})

            fn("get_time") { ->
                return@fn valueOf(System.currentTimeMillis().toDouble())
            }
        }
        env.set("mc", mc)
        env.set("dofile", ARequire.dofile)
        env.set("require", ARequire.require)
        return mc
    }
}