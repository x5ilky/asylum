package tech.silky.asylum.client

import org.luaj.vm2.Globals
import org.luaj.vm2.LoadState
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.*
import org.luaj.vm2.lib.jse.JseBaseLib
import org.luaj.vm2.lib.jse.JseMathLib
import org.luaj.vm2.lib.jse.JsePlatform
import tech.silky.asylum.client.std.Events


data class AsylumHook (val hook: (a: AsylumLua) -> Unit, val id: Int) {}
object AsylumLua {
    lateinit var globals: Globals
    private var hooks: MutableList<AsylumHook> = mutableListOf()
    private var hooksCounter = 0
    fun init() {
        globals = Globals()

        globals.load(JseBaseLib())
        globals.load(PackageLib())
        globals.load(Bit32Lib())
        globals.load(TableLib())
        globals.load(StringLib())
        globals.load(CoroutineLib())
        globals.load(JseMathLib())
        LoadState.install(globals)
        LuaC.install(globals)
    }

    fun hook(hook: (a: AsylumLua) -> Unit): Int {
        val id = hooksCounter++
        hooks.add(AsylumHook(hook = hook, id = id))
        return id
    }
    fun unhook(id: Int) {
        hooks.removeIf { it.id == id }
    }
    fun reload() {
        Events.blockBreakEvents.clear()
        for (hook in hooks) {
            hook.hook(this)
        }
    }
}