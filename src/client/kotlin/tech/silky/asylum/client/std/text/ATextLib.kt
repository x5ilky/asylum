package tech.silky.asylum.client.std.text

import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.luaj.vm2.Lua
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import tech.silky.asylum.client.IOBJ
import tech.silky.asylum.client.inner
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.ATypes
import tech.silky.asylum.client.typecheck
import java.util.Optional

object ATextLib {
    fun make(text: Text): LuaValue {
        val value = luaTable {
            value("__type", LuaValue.valueOf(ATypes.TEXT))
            attach(IOBJ, text)
            metatable("__index", lib)
        }

        return value
    }

    val lib = luaTable {
        fn("literal") { str ->
            typecheck { str with ATypes.STRING }
            return@fn make(Text.literal(str.toString()))
        }
        fn("append") { rawText, rawOtherText ->
            typecheck { rawText with ATypes.TEXT; rawOtherText with ATypes.TEXT }
            val text = rawText.inner<Text>(IOBJ)
            val othertext = rawOtherText.inner<Text>(IOBJ)
            val new = (text as MutableText).append(othertext)
            return@fn make(new)
        }
        fn("get_style") { rawText ->
            typecheck { rawText with ATypes.TEXT }
            val text = rawText.inner<Text>(IOBJ)
            return@fn ATextStyle.make(text.style)
        }
        fn("with_style") { rawText, style ->
            typecheck { rawText with ATypes.TEXT; style with ATypes.TEXT_STYLE }
            val text = rawText.inner<Text>(IOBJ)
            val style = ATextStyle.from(style)
            val new = (text as MutableText).setStyle(style)

            return@fn make(new)
        }
        fn("clear_style") { rawText ->
            typecheck { rawText with ATypes.TEXT }
            val text = rawText.inner<Text>(IOBJ)
            val new = (text as MutableText).setStyle(Style.EMPTY)
            return@fn make(new)
        }
        fn("visit") { rawText, fn ->
            typecheck { rawText with ATypes.TEXT; fn with ATypes.FUNCTION }
            val text = rawText.inner<Text>(IOBJ)
            text.visit({ style, str ->
                fn.call(ATextStyle.make(style), LuaValue.valueOf(str))

                Optional.empty<Any>()
            }, Style.EMPTY)
            return@fn LuaValue.NIL
        }
        fn("tostring") { self ->
            typecheck { self with ATypes.TEXT }
            val text = self.inner<Text>(IOBJ)
            return@fn LuaValue.valueOf(text.toString())
        }
    }
}