package tech.silky.asylum.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.luaj.vm2.LuaValue
import tech.silky.asylum.client.events.ClientBlockBreakListener
import tech.silky.asylum.client.events.ClientDamageEntityListener
import tech.silky.asylum.client.std.AMinecraftLib
import java.nio.charset.StandardCharsets


class AsylumClient : ClientModInitializer {
    val MOD_ID = "asylum"
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    var scripts = mutableMapOf<Identifier, LuaValue>()

    override fun onInitializeClient() {
        AsylumLua.init()
        AsylumLua.hook { alua ->
            alua.globals.load(AMinecraftLib)
        }

        ClientBlockBreakListener.init()
        ClientDamageEntityListener.init()

        ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(
            Identifier.of(MOD_ID, "lua_reloader"),
            object : SynchronousResourceReloader {
                override fun reload(manager: ResourceManager) {
                    AsylumLua.reload()
                    loadAllLua(manager)
                    runAllLua()
                }
            }
        )
        HudRenderCallback.EVENT.register { drawContext, tickDelta ->
            val client = MinecraftClient.getInstance()
            val textRenderer = client.textRenderer

            drawContext.drawText(
                textRenderer,
                "Hello world",
                10,
                10,
                0xFFFFFF,
                true
            )
        }
    }

    private fun loadAllLua(manager: ResourceManager) {
        scripts.clear()
        val luaFolder = "lua" // inside assets/mymod/lua/

        manager.findResources(luaFolder) { id ->
            id.path.endsWith(".lua")
        }.forEach { (id, resource) ->
            val content = resource.inputStream.readBytes().toString(StandardCharsets.UTF_8)

            scripts[id] = AsylumLua.globals.load(content, id.toString())
            println("Loaded Lua: $id (${content.length} bytes)")
        }
    }
    private fun runAllLua() {
        val player = MinecraftClient.getInstance().player
        for ((k, v) in scripts) {
            player?.sendMessage(Text.of("Executing script: $k"), false)
            tryCall {
                v.call()
            }
        }
    }
}