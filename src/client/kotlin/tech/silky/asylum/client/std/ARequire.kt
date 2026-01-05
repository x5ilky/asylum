package tech.silky.asylum.client.std

import net.minecraft.util.Identifier
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import tech.silky.asylum.client.AsylumClient
import tech.silky.asylum.client.AsylumLua
import tech.silky.asylum.client.tcFor
import tech.silky.asylum.client.typecheck
import tech.silky.asylum.client.util.PathHelper

object ARequire {
    val dofile = object : OneArgFunction() {
        override fun call(arg: LuaValue): LuaValue {
            typecheck { arg with (ATypes.IDENTIFIER or tcFor(ATypes.STRING)) }
            var iden: Identifier
            if (arg.isstring()) {
                // string
                iden = Identifier.of(arg.toString())
            } else {
                // table
                iden = AIdentifier.from(arg)
            }
            if (AsylumClient.scripts.containsKey(iden)) {
                return AsylumClient.scripts[iden]!!.call()
            }

            return LuaValue.NIL
        }
    }
    val require = object : OneArgFunction() {
        override fun call(arg: LuaValue): LuaValue {
            typecheck { arg with (ATypes.IDENTIFIER or tcFor(ATypes.STRING)) }
            val idens = mutableListOf<Identifier>()
            val suffixes = mutableListOf<String>()

            if (arg.isstring()) {
                if (arg.toString().contains(":")) {
                    idens += Identifier.of(arg.toString())
                } else suffixes += arg.toString();
            } else {
                // table
                idens += AIdentifier.from(arg)
            }

            val mc = AsylumLua.globals.get("mc")
            val mp = mc.get("module_path")

            for (i in 1..mp.length()) {
                val prefix = mp.get(i).toString()

                for (suffix in suffixes) {
                    if (PathHelper.isValidIdentifier(
                        prefix + suffix
                    )) {
                        idens.add(Identifier.of(prefix + suffix))
                    }
                }
            }

            for (iden in idens) {
                if (AsylumClient.scripts.containsKey(iden)) {
                    val module = mc.get("modules").get(iden.toString())
                    if (module.isnil()) {
                        val value = AsylumClient.scripts[iden]!!.call()
                        mc.get("modules").set(iden.toString(), value)
                        return value
                    } else return module
                }
            }

            return LuaValue.NIL
        }
    }
}