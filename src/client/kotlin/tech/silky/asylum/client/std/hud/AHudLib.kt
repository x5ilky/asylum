package tech.silky.asylum.client.std.hud

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaValue
import tech.silky.asylum.client.AsylumClient
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.misc.AsylumRuntimeData
import tech.silky.asylum.client.std.AIdentifier
import tech.silky.asylum.client.std.ATypes
import tech.silky.asylum.client.tryCall
import tech.silky.asylum.client.typecheck
import kotlin.toString

object AHudLib {
    val guiLayers = mutableMapOf<Int, Identifier>()
    var guiCount = 0

    private fun id(id: Identifier) = AIdentifier.make(id.namespace, id.path)
    val lib = luaTable {
        value("layers", luaTable {
            value("misc_overlays", id(Identifier.ofVanilla("misc_overlays")))
            value("crosshair", id(Identifier.ofVanilla("crosshair")))
            value("spectator_menu", id(Identifier.ofVanilla("spectator_menu")))
            value("hotbar", id(Identifier.ofVanilla("hotbar")))
            value("armor_bar", id(Identifier.ofVanilla("armor_bar")))
            value("health_bar", id(Identifier.ofVanilla("health_bar")))
            value("food_bar", id(Identifier.ofVanilla("food_bar")))
            value("air_bar", id(Identifier.ofVanilla("air_bar")))
            value("mount_health", id(Identifier.ofVanilla("mount_health")))
            value("info_bar", id(Identifier.ofVanilla("info_bar")))
            value("experience_level", id(Identifier.ofVanilla("experience_level")))
            value("held_item_tooltip", id(Identifier.ofVanilla("held_item_tooltip")))
            value("spectator_tooltip", id(Identifier.ofVanilla("spectator_tooltip")))
            value("status_effects", id(Identifier.ofVanilla("status_effects")))
            value("boss_bar", id(Identifier.ofVanilla("boss_bar")))
            value("sleep", id(Identifier.ofVanilla("sleep")))
            value("demo_timer", id(Identifier.ofVanilla("demo_timer")))
            value("scoreboard", id(Identifier.ofVanilla("scoreboard")))
            value("overlay_message", id(Identifier.ofVanilla("overlay_message")))
            value("title_and_subtitle", id(Identifier.ofVanilla("title_and_subtitle")))
            value("chat", id(Identifier.ofVanilla("chat")))
            value("player_list", id(Identifier.ofVanilla("player_list")))
            value("subtitles", id(Identifier.ofVanilla("subtitles")))
        })
        value("draw_context", ADrawContext.lib)

        fn("add_before") { layerRaw, fn ->
            typecheck { layerRaw with ATypes.IDENTIFIER; fn with ATypes.FUNCTION }
            val layer = AIdentifier.from(layerRaw)
            val c = guiCount++
            val id = Identifier.of(AsylumClient.MOD_ID, "asylum_userguilayer$c")
            guiLayers[c] = id

            HudElementRegistry.attachElementBefore(layer, id) { context, tickCounter ->
                tryCall {
                    fn.call(
                        ADrawContext.make(context),
                        LuaValue.valueOf(tickCounter.dynamicDeltaTicks.toDouble())
                    )
                }
            }

            return@fn LuaValue.valueOf(c)
        }
        fn("add_after") { layerRaw, fn ->
            typecheck { layerRaw with ATypes.IDENTIFIER; fn with ATypes.FUNCTION }
            val layer = AIdentifier.from(layerRaw)
            val c = guiCount++
            val id = Identifier.of(AsylumClient.MOD_ID, "asylum_userguilayer$c")
            guiLayers[c] = id

            HudElementRegistry.attachElementAfter(layer, id) { context, tickCounter ->
                tryCall {
                    fn.call(
                        ADrawContext.make(context),
                        LuaValue.valueOf(tickCounter.dynamicDeltaTicks.toDouble())
                    )
                }
            }

            return@fn LuaValue.valueOf(c)
        }
        fn("remove") { id ->
            typecheck { id with ATypes.INTEGER }
            if (guiLayers.containsKey(id.toint())) {
                HudElementRegistry.removeElement(guiLayers[id.toint()]!!)
                guiLayers.remove(id.toint())
            }

            return@fn LuaValue.NIL
        }

        fn("get_screen_width") { ->
            val client = MinecraftClient.getInstance()
            return@fn LuaValue.valueOf(client.window.width)
        }
        fn("get_screen_height") { ->
            val client = MinecraftClient.getInstance()
            return@fn LuaValue.valueOf(client.window.height)
        }
        fn("get_scaled_screen_width") { ->
            val client = MinecraftClient.getInstance()
            return@fn LuaValue.valueOf(client.window.scaledWidth)
        }
        fn("get_scaled_screen_height") { ->
            val client = MinecraftClient.getInstance()
            return@fn LuaValue.valueOf(client.window.scaledHeight)
        }

        fn("show") { type ->
            val key = type.toString()
            if (!AsylumRuntimeData.displayFlags.containsKey(key))
                throw LuaError("Cannot show `$type`; it is not a vanilla HUD element")
            AsylumRuntimeData.displayFlags[key] = true
            return@fn LuaValue.NIL
        }
        fn("hide") { type ->
            val key = type.toString()
            if (!AsylumRuntimeData.displayFlags.containsKey(key))
                throw LuaError("Cannot show `$type`; it is not a vanilla HUD element")
            AsylumRuntimeData.displayFlags[key] = false
            return@fn LuaValue.NIL
        }
        fn("is_hidden") { type ->
            val key = type.toString()
            if (!AsylumRuntimeData.displayFlags.containsKey(key))
                throw LuaError("Cannot show `$type`; it is not a vanilla HUD element")

            return@fn LuaValue.valueOf(AsylumRuntimeData.displayFlags[key]!!)
        }
    }
}