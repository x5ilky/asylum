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
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.AIdentifier
import tech.silky.asylum.client.std.position.AWorld
import kotlin.random.Random

object ADrawContext {
    fun make(ctx: DrawContext): LuaValue {
        val table = luaTable {
            attach("__java", ctx)
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
            val ctx = self.inner<DrawContext>("__java")
            ctx.fill(x1.toint(), y1.toint(), x2.toint(), y2.toint(), color.toint())

            return@fn LuaValue.NIL
        }
        fn("draw_text") { self, text, x1, y1, color ->
            val ctx = self.inner<DrawContext>("__java")

            val client = MinecraftClient.getInstance();
            val textRenderer = client.textRenderer

            ctx.drawText(textRenderer, Text.of(text.toString()), x1.toint(), y1.toint(), color.toint(), false)

            return@fn LuaValue.NIL
        }
        fn("draw_texture") { self, text, opts ->
            val ctx = self.inner<DrawContext>("__java")

            val texture = AIdentifier.from(text)

            ctx.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture,
                opts.get("texture_width").toint(),
                opts.get("texture_height").toint(),
                opts.get("u").toint(),
                opts.get("v").toint(),
                opts.get("x").toint(),
                opts.get("y").toint(),
                opts.get("width").toint(),
                opts.get("height").toint(),
            )

            return@fn LuaValue.NIL
        }
        fn("get_text_width") { self, text ->
            val client = MinecraftClient.getInstance();
            val textRenderer = client.textRenderer

            return@fn LuaValue.valueOf(textRenderer.getWidth(text.toString()))
        }
        fn("get_text_height") { self ->
            val client = MinecraftClient.getInstance();
            val textRenderer = client.textRenderer


            return@fn LuaValue.valueOf(textRenderer.fontHeight)
        }


        fn("matrix_push") { self ->
            val ctx = self.inner<DrawContext>("__java")
            ctx.matrices.pushMatrix()
            return@fn LuaValue.NIL
        }
        fn("matrix_pop") { self ->
            val ctx = self.inner<DrawContext>("__java")
            ctx.matrices.popMatrix()
            return@fn LuaValue.NIL
        }
        fn("matrix_translate") { self, x, y ->
            val ctx = self.inner<DrawContext>("__java")
            ctx.matrices.translate(x.tofloat(), y.tofloat())
            return@fn LuaValue.NIL
        }
        fn("matrix_scale") { self, x, y ->
            val ctx = self.inner<DrawContext>("__java")
            ctx.matrices.scale(x.tofloat(), y.tofloat())
            return@fn LuaValue.NIL
        }
        fn("matrix_rotate") { self, ang ->
            val ctx = self.inner<DrawContext>("__java")
            ctx.matrices.rotate(ang.tofloat())
            return@fn LuaValue.NIL
        }
        fn("matrix_rotate_about") { self, ang, x, y ->
            val ctx = self.inner<DrawContext>("__java")
            ctx.matrices.rotateAbout(ang.tofloat(), x.tofloat(), y.tofloat())
            return@fn LuaValue.NIL
        }
    }
}