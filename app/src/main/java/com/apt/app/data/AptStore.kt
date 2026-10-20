package com.apt.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/** Guarda el estado en el teléfono (SharedPreferences + JSON). */
class AptStore(context: Context) {
    private val prefs = context.getSharedPreferences("apt", Context.MODE_PRIVATE)

    fun loadProfile(): Profile {
        val raw = prefs.getString("profile", null) ?: return Profile()
        val o = JSONObject(raw)
        return Profile(
            linked = o.optBoolean("linked"),
            myCode = o.optString("myCode"),
            partnerCode = o.optString("partnerCode"),
            since = LocalDate.parse(o.optString("since", "2023-02-14")),
            me = o.optString("me", "Alan"),
            partner = o.optString("partner", "Paulina"),
            nickForPartner = o.optString("nickForPartner"),
            nickForMe = o.optString("nickForMe"),
            avisoNotas = o.optBoolean("avisoNotas", true),
            avisoRecuerdos = o.optBoolean("avisoRecuerdos", true),
            avisoAniversarios = o.optBoolean("avisoAniversarios", true),
            coverPhoto = o.optStringOrNull("coverPhoto"),
        )
    }

    fun saveProfile(p: Profile) {
        val o = JSONObject()
            .put("linked", p.linked)
            .put("myCode", p.myCode)
            .put("partnerCode", p.partnerCode)
            .put("since", p.since.toString())
            .put("me", p.me)
            .put("partner", p.partner)
            .put("nickForPartner", p.nickForPartner)
            .put("nickForMe", p.nickForMe)
            .put("avisoNotas", p.avisoNotas)
            .put("avisoRecuerdos", p.avisoRecuerdos)
            .put("avisoAniversarios", p.avisoAniversarios)
            .put("coverPhoto", p.coverPhoto ?: JSONObject.NULL)
        prefs.edit().putString("profile", o.toString()).apply()
    }

    fun loadPlaces() = loadList("places") {
        Place(
            it.getLong("id"), it.getString("name"), it.getString("region"), it.getInt("km"),
            it.getDouble("x").toFloat(), it.getDouble("y").toFloat(),
            PlaceStatus.valueOf(it.getString("status")), it.optString("note"),
        )
    }

    fun savePlaces(items: List<Place>) = saveList("places", items) {
        JSONObject().put("id", it.id).put("name", it.name).put("region", it.region).put("km", it.km)
            .put("x", it.x.toDouble()).put("y", it.y.toDouble()).put("status", it.status.name).put("note", it.note)
    }

    fun loadMemories() = loadList("memories") {
        Memory(
            it.getLong("id"), it.getString("title"), LocalDate.parse(it.getString("date")), it.optString("place"),
            MemoryKind.valueOf(it.getString("kind")), it.optStringOrNull("uri"), it.optString("text"),
        )
    }

    fun saveMemories(items: List<Memory>) = saveList("memories", items) {
        JSONObject().put("id", it.id).put("title", it.title).put("date", it.date.toString()).put("place", it.place)
            .put("kind", it.kind.name).put("uri", it.uri ?: JSONObject.NULL).put("text", it.text)
    }

    fun loadCorner() = loadList("corner") {
        CornerItem(
            it.getLong("id"), CornerKind.valueOf(it.getString("kind")), it.getString("author"), it.getString("text"),
            it.optString("extra"), LocalDate.parse(it.getString("date")), it.optBoolean("done"), it.optStringOrNull("uri"),
        )
    }

    fun saveCorner(items: List<CornerItem>) = saveList("corner", items) {
        JSONObject().put("id", it.id).put("kind", it.kind.name).put("author", it.author).put("text", it.text)
            .put("extra", it.extra).put("date", it.date.toString()).put("done", it.done).put("uri", it.uri ?: JSONObject.NULL)
    }

    fun loadNotices() = loadList("notices") {
        Notice(it.getLong("id"), it.getString("text"), it.getString("time"), it.optBoolean("read"))
    }

    fun saveNotices(items: List<Notice>) = saveList("notices", items) {
        JSONObject().put("id", it.id).put("text", it.text).put("time", it.time).put("read", it.read)
    }

    private fun <T> loadList(key: String, parse: (JSONObject) -> T): List<T>? {
        val raw = prefs.getString(key, null) ?: return null
        val array = JSONArray(raw)
        return (0 until array.length()).map { parse(array.getJSONObject(it)) }
    }

    private fun <T> saveList(key: String, items: List<T>, toJson: (T) -> JSONObject) {
        val array = JSONArray()
        items.forEach { array.put(toJson(it)) }
        prefs.edit().putString(key, array.toString()).apply()
    }

    private fun JSONObject.optStringOrNull(key: String): String? =
        if (isNull(key)) null else optString(key).ifEmpty { null }
}
