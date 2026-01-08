package tech.silky.asylum.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.ZeroArgFunction
import tech.silky.asylum.client.events.ClientBlockBreakListener
import tech.silky.asylum.client.events.ClientDamageEntityListener
import tech.silky.asylum.client.events.ClientReceiveMessageListener
import tech.silky.asylum.client.misc.LuaModule
import tech.silky.asylum.client.std.AMinecraftLib
import java.nio.charset.StandardCharsets
import java.util.Optional


class AsylumClient : ClientModInitializer {
    companion object {
        const val MOD_ID = "asylum"
        val LOGGER: Logger = LogManager.getLogger(MOD_ID)
        var scripts = mutableMapOf<Identifier, LuaValue>()

        lateinit var VERSION: String;
    }


    override fun onInitializeClient() {
        val mod: Optional<ModContainer> = FabricLoader.getInstance().getModContainer(MOD_ID)

        mod.ifPresent { container ->
            VERSION = container.metadata.version.friendlyString
        }

        AsylumLua.init()
        AsylumLua.hook { alua ->
            alua.globals.set("package", LuaValue.NIL)
            alua.globals.load(AMinecraftLib)
        }

        ClientBlockBreakListener.init()
        ClientDamageEntityListener.init()
        ClientReceiveMessageListener.init()

        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(
            Identifier.of(MOD_ID, "lua_reloader"),
            object : SynchronousResourceReloader {
                override fun reload(manager: ResourceManager) {
                    AsylumLua.reload()
                    loadAllLua(manager)
                    runAllModules()
                }
            }
        )
    }

    private fun loadAllLua(manager: ResourceManager) {
        scripts.clear()
        val luaFolder = "lua" // inside assets/mymod/lua/

        manager.findResources(luaFolder) { id ->
            id.path.endsWith(".lua")
        }.forEach { (id, resource) ->
            val content = resource.inputStream.readBytes().toString(StandardCharsets.UTF_8)

            scripts[id] = object : ZeroArgFunction() {
                override fun call(): LuaValue {
                    // this will be commented out for the time being
                    // since i cant get this to always transfer when you call a function
                    // created in a file
//                    val mp = AsylumLua.globals.get("mc").get("module_path")
//                    val restore = mp.length()
//                    mp.set(mp.length()+1, valueOf("${id.namespace}:"))
//                    mp.set(mp.length()+1, valueOf(PathHelper.getFolderFromPath(id.toString())))

                    val v = AsylumLua.globals.load(content, id.toString()).call()

//                    for (i in restore+1..mp.length()) {
//                        mp.set(i, NIL)
//                    }
                    return v
                }
            }
            println("Loaded Lua: $id (${content.length} bytes)")
        }
    }
    private fun runAllModules() {
        val mods = mutableMapOf<String, LuaModule>()

        val player = MinecraftClient.getInstance().player
        val mc = MinecraftClient.getInstance()
        for ((k, v) in scripts) {
            if (k.path == "lua/module.lua") {
                val module = tryCall {
                    v.call()
                }
                if (module == null || module.isnil()) {
                    player?.sendMessage(Text.literal("Failed to load module"), false)
                    mc.toastManager.add(
                        SystemToast.create(mc, SystemToast.Type.NARRATOR_TOGGLE,
                            Text.literal("Hello World!"), Text.literal("This is a toast."))
                    )
                    continue
                }

                val dependencies = mutableListOf<String>()
                val rawDependencies = module.get("dependencies")
                if (!rawDependencies.isnil() && rawDependencies.istable()) {
                   for (i in 1..rawDependencies.length()) {
                       dependencies += rawDependencies.get(i).toString()
                   }
                }

                val initFunc = module.get("init")
                if (!initFunc.isfunction()) {
                    player?.sendMessage(Text.literal("Failed to load module - Module has no init function"), false)
                    mc.toastManager.add(
                        SystemToast.create(mc, SystemToast.Type.NARRATOR_TOGGLE,
                            Text.literal("Hello World!"), Text.literal("This is a toast."))
                    )
                    continue
                }

                mods[k.namespace] = LuaModule(
                    id = k.namespace,
                    dependencies = dependencies,
                    init = initFunc
                )
            }
        }

        val ran = mutableMapOf<String, Boolean>()
        for ((k, _) in mods) ran[k] = false

        val modules = AsylumLua.globals.get("mc").get("modules")
        val keys = mods.keys.toList()

        fun dfs(key: String, trace: List<String>): Boolean {
            if (ran[key]!!) return true;
            for (dep in mods[key]!!.dependencies) {
                if (ran[dep]!!) continue;
                if (trace.contains(dep)) {
                    // cycle detected
                    val message = listOf("Circular dependency list has been detected!",
                        trace.joinToString(" -> "))
                    for (ln in message)
                        player?.sendMessage(Text.literal(ln), false)
                    return false
                }
                if (!dfs(dep, listOf(*trace.toTypedArray(), dep))) {
                    return false
                }
            }
            ran[key] = true


            val value = tryCall {
                mods[key]!!.init.call()
            }
            if (value != LuaValue.NIL && value != null) {
                modules.set(key, value)
                return true;
            } else return false;
        }
        for (i in 0..<keys.size) {
            if (!ran[keys[i]]!!) {
                dfs(keys[i], listOf(keys[i]))
            }
        }
    }
}