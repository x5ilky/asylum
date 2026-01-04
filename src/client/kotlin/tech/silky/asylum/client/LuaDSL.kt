package tech.silky.asylum.client

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.ThreeArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.ZeroArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import tech.silky.asylum.client.std.ATypes
import kotlin.math.abs

class LuaTableBuilder {
    private val table = LuaTable()
    private var metatable: LuaTable? = null

    fun fn(name: String, body: () -> LuaValue) {
        table[name] = object : ZeroArgFunction() {
            override fun call(): LuaValue = body()
        }
    }

    fun fn(name: String, body: (LuaValue) -> LuaValue) {
        table[name] = object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue = body(arg)
        }
    }

    fun fn(name: String, body: (LuaValue, LuaValue) -> LuaValue) {
        table[name] = object : TwoArgFunction() {
            override fun call(a: LuaValue, b: LuaValue): LuaValue = body(a, b)
        }
    }
    fun fn(name: String, body: (LuaValue, LuaValue, LuaValue) -> LuaValue) {
        table[name] = object : ThreeArgFunction() {
            override fun call(a: LuaValue, b: LuaValue, c: LuaValue): LuaValue = body(a, b, c)
        }
    }
    fun fn(name: String, body: (LuaValue, LuaValue, LuaValue, LuaValue) -> LuaValue) {
        table[name] = object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                return body(
                    args.arg(1),
                    args.arg(2),
                    args.arg(3),
                    args.arg(4),
                )
            }
        }
    }
    fun fn(name: String, body: (LuaValue, LuaValue, LuaValue, LuaValue, LuaValue) -> LuaValue) {
        table[name] = object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                return body(
                    args.arg(1),
                    args.arg(2),
                    args.arg(3),
                    args.arg(4),
                    args.arg(5),
                )
            }
        }
    }
    fun fn(name: String, body: (LuaValue, LuaValue, LuaValue, LuaValue, LuaValue, LuaValue) -> LuaValue) {
        table[name] = object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                return body(
                    args.arg(1),
                    args.arg(2),
                    args.arg(3),
                    args.arg(4),
                    args.arg(5),
                    args.arg(6),
                )
            }
        }
    }

    fun value(name: String, v: LuaValue) {
        table[name] = v
    }
    fun get(name: String): LuaValue {
        return table.get(name)
    }

    fun attach(key: String, value: Any) {
        val mt = metatable ?: LuaTable().also {
            // lock metatable from Lua
            it["__metatable"] = LuaValue.FALSE
            metatable = it
        }

        mt[key] = CoerceJavaToLua.coerce(value)
    }

    fun metatable(key: String, value: LuaValue) {
        val mt = metatable ?: LuaTable()
        mt[key] = value
        metatable = mt
    }

    fun build(): LuaTable {
        metatable?.let { table.setmetatable(it) }
        return table
    }
}

fun luaTable(init: LuaTableBuilder.() -> Unit): LuaTable =
    LuaTableBuilder().apply(init).build()

@DslMarker
annotation class PairDsl

@PairDsl
class PairBuilder<A> {
    internal val list = mutableListOf<Pair<A, Typechecker>>()

    infix fun A.with(b: Typechecker) {
        list += this to b
    }
    infix fun A.with(b: String) {
        list += this to Typechecker(
            when (b) {
                ATypes.BOOLEAN -> { v: LuaValue -> v.isboolean() to "expected boolean" }
                ATypes.INTEGER -> { v: LuaValue -> v.isnumber() to "expected integer" }
                ATypes.DOUBLE -> { v: LuaValue -> v.isnumber() to "expected number" }
                ATypes.STRING -> { v: LuaValue -> v.isstring() to "expected string" }
                ATypes.FUNCTION -> { v: LuaValue -> v.isfunction() to "expected function" }
                ATypes.TABLE -> { v: LuaValue -> v.istable() to "expected table" }
                ATypes.NIL -> { v: LuaValue -> v.isnil() to "expected nil" }
                else ->
                    { v: LuaValue -> (v.get("__type").toString() == b) to "expected $b" }
            },
            b
        )
    }
}

const val IOBJ = "__iobj"
data class Typechecker (
    val validator: (LuaValue) -> Pair<Boolean, String>,
    val name: String
)
fun typecheck(block: PairBuilder<LuaValue>.() -> Unit) {
    val l = PairBuilder<LuaValue>().apply(block).list
    var i = 0
    for ((vl, typ) in l) {
        i++
        val result = typ.validator(vl)
        if (!result.first)
            throw LuaError("Mismatched argument type, argument $i expected ${typ.name}, got ${vl.typename()}\n${result.second}")
    }
}
fun tableOf(typename: String, block: PairBuilder<String>.() -> Unit): Typechecker {
    return Typechecker({ table: LuaValue ->
        val l = PairBuilder<String>().apply(block).list
        var i = 0
        for ((vl, typ) in l) {
            i++
            val result = typ.validator(table.get(vl))
            if (!result.first)
                return@Typechecker false to "Mismatched argument type, argument $i expected ${typ.name}, got ${table.get(vl).typename()}\n" + result.second
        }
        return@Typechecker true to ""
    }, typename)
}

inline fun <reified T> LuaValue.inner(name: String): T = this.getmetatable().get(name).touserdata(T::class.java) as T
fun tryCall(v: () -> Unit) {
    try {
        v()
    } catch (e: LuaError) {
        println(e)
        val player = MinecraftClient.getInstance().player
        player?.sendMessage(Text.of("Error while running lua script"), false)
        for (ln in e.toString().lines()) {
            player?.sendMessage(Text.of(ln), false)
        }
    }
}