package tech.silky.asylum.client.std.hud

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.valueOf
import org.luaj.vm2.lib.TwoArgFunction
import tech.silky.asylum.client.IOBJ
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.AIdentifier
import tech.silky.asylum.client.std.ATypes
import tech.silky.asylum.client.std.position.AWorld
import tech.silky.asylum.client.tableOf
import tech.silky.asylum.client.typecheck
import kotlin.random.Random

object ADrawContext {
    fun make(ctx: DrawContext): LuaValue {
        val table = luaTable {
            value("__type", LuaValue.valueOf(ATypes.DRAW_CONTEXT))
            attach(IOBJ, ctx)
            metatable("__index", lib)
            metatable("__tostring", object : TwoArgFunction() {
                override fun call(table: LuaValue, unused: LuaValue?): LuaValue? {
                    return valueOf("<java:DrawContext>")
                }
            })
        }

        return table
    }
    val lib = luaTable {
        fn("draw_rectangle") { self, x1, y1, x2, y2, color ->
            typecheck {
                self with ATypes.DRAW_CONTEXT
                x1 with ATypes.INTEGER
                y1 with ATypes.INTEGER
                x2 with ATypes.INTEGER
                y2 with ATypes.INTEGER
                color with ATypes.INTEGER
            }
            val ctx = self.inner<DrawContext>(IOBJ)
            ctx.fill(x1.toint(), y1.toint(), x2.toint(), y2.toint(), color.toint())

            return@fn LuaValue.NIL
        }
        fn("draw_text") { self, text, x1, y1, color ->
            typecheck {
                self with ATypes.DRAW_CONTEXT
                text with ATypes.TEXT
                x1 with ATypes.INTEGER
                y1 with ATypes.INTEGER
                color with ATypes.INTEGER
            }

            val text = text.inner<Text>(IOBJ)
            val ctx = self.inner<DrawContext>(IOBJ)

            val client = MinecraftClient.getInstance();
            val textRenderer = client.textRenderer

            ctx.drawText(textRenderer, text, x1.toint(), y1.toint(), color.toint(), false)

            return@fn LuaValue.NIL
        }
        fn("draw_texture") { self, text, opts ->
            typecheck {
                self with ATypes.DRAW_CONTEXT
                text with ATypes.IDENTIFIER
                opts with tableOf("draw_texture options") {
                    "u" with ATypes.INTEGER
                    "v" with ATypes.INTEGER
                    "x" with ATypes.INTEGER
                    "y" with ATypes.INTEGER
                    "width" with ATypes.INTEGER
                    "height" with ATypes.INTEGER
                    "texture_width" with ATypes.INTEGER
                    "texture_height" with ATypes.INTEGER
                }

            }
            val ctx = self.inner<DrawContext>(IOBJ)

            val texture = AIdentifier.from(text)


            ctx.drawTexture(RenderPipelines.GUI_TEXTURED,
                texture,
                opts.get("x").toint(),
                opts.get("y").toint(),
                opts.get("u").tofloat(),
                opts.get("v").tofloat(),
                opts.get("width").toint(),
                opts.get("height").toint(),
                opts.get("texture_width").toint(),
                opts.get("texture_height").toint(),
            )

            return@fn LuaValue.NIL
        }
        fn("get_string_width") { text ->
            typecheck {
                text with ATypes.STRING
            }
            val client = MinecraftClient.getInstance();
            val textRenderer = client.textRenderer

            return@fn LuaValue.valueOf(textRenderer.getWidth(text.toString()))
        }
        fn("get_string_height") { self ->
            val client = MinecraftClient.getInstance();
            val textRenderer = client.textRenderer

            return@fn LuaValue.valueOf(textRenderer.fontHeight)
        }


        fn("matrix_push") { self ->
            typecheck { self with ATypes.DRAW_CONTEXT }
            val ctx = self.inner<DrawContext>(IOBJ)
            ctx.matrices.pushMatrix()
            return@fn LuaValue.NIL
        }
        fn("matrix_pop") { self ->
            typecheck { self with ATypes.DRAW_CONTEXT }
            val ctx = self.inner<DrawContext>(IOBJ)
            ctx.matrices.popMatrix()
            return@fn LuaValue.NIL
        }
        fn("matrix_translate") { self, x, y ->
            typecheck {
                self with ATypes.DRAW_CONTEXT
                x with ATypes.DOUBLE
                y with ATypes.DOUBLE
            }
            val ctx = self.inner<DrawContext>(IOBJ)
            ctx.matrices.translate(x.tofloat(), y.tofloat())
            return@fn LuaValue.NIL
        }
        fn("matrix_scale") { self, x, y ->
            typecheck {
                self with ATypes.DRAW_CONTEXT
                x with ATypes.DOUBLE
                y with ATypes.DOUBLE
            }
            val ctx = self.inner<DrawContext>(IOBJ)
            ctx.matrices.scale(x.tofloat(), y.tofloat())
            return@fn LuaValue.NIL
        }
        fn("matrix_rotate") { self, ang ->
            typecheck {
                self with ATypes.DRAW_CONTEXT
                ang with ATypes.DOUBLE
            }
            val ctx = self.inner<DrawContext>(IOBJ)
            ctx.matrices.rotate(ang.tofloat())
            return@fn LuaValue.NIL
        }
        fn("matrix_rotate_about") { self, ang, x, y ->
            typecheck {
                self with ATypes.DRAW_CONTEXT
                ang with ATypes.DOUBLE
                x with ATypes.DOUBLE
                y with ATypes.DOUBLE
            }
            val ctx = self.inner<DrawContext>(IOBJ)
            ctx.matrices.rotateAbout(ang.tofloat(), x.tofloat(), y.tofloat())
            return@fn LuaValue.NIL
        }
    }
}