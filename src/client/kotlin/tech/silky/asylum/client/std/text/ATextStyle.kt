package tech.silky.asylum.client.std.text

import net.minecraft.text.Style
import org.luaj.vm2.LuaValue
import tech.silky.asylum.client.luaTable
import tech.silky.asylum.client.std.ATypes

object ATextStyle {
    fun make(style: Style): LuaValue {
        return luaTable {
            value("bold", LuaValue.valueOf(style.isBold))
            value("italic", LuaValue.valueOf(style.isItalic))
            value("underline", LuaValue.valueOf(style.isUnderlined))
            value("obfuscated", LuaValue.valueOf(style.isObfuscated))
            value("color", LuaValue.valueOf(style.getColor()?.rgb ?: 0xffffff))
        }
    }
    fun from(value: LuaValue): Style {
        return Style.EMPTY
            .withBold(value.get("bold").toboolean())
            .withItalic(value.get("italic").toboolean())
            .withUnderline(value.get("underline").toboolean())
            .withObfuscated(value.get("obfuscated").toboolean())
            .withColor(value.get("color").toint())
    }
}