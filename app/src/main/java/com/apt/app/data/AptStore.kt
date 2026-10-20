package com.apt.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/** Guarda el estado en el teléfono (SharedPreferences + JSON). */
class AptStore(context: Context) {
    // "apt-v2": el formato cambió con el diseño APT Concepto; lo guardado antes se ignora.
    private val prefs = context.getSharedPreferences("apt-v2", Context.MODE_PRIVATE)

    fun loadProfile(): Profile? {
        val raw = prefs.getString("profile", null) ?: return null
        return runCatching {
            val o = JSONObject(raw)
            Profile(
                linked = o.optBoolean("linked"),
                myCode = o.optString("myCode"),
                partnerCode = o.optString("partnerCode"),
                since = LocalDate.parse(o.optString("since", "2023-02-14")),
                me = o.optString("me", "Alan"),
                partner = o.optString("partner", "Paulina"),
                nickForPartner = o.optString("nickForPartner"),
                nickForMe = o.optString("nickForMe"),
                nextPlaceId = o.optStringOrNull("nextPlaceId"),
                coverPhoto = o.optStringOrNull("coverPhoto"),
                avisoNuevos = o.optBoolean("avisoNuevos", true),
                avisoAniversarios = o.optBoolean("avisoAniversarios", true),
                avisoVoz = o.optBoolean("avisoVoz", true),
                avisoUnDiaComoHoy = o.optBoolean("avisoUnDiaComoHoy", false),
            )
        }.getOrNull()
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
            .put("nextPlaceId", p.nextPlaceId ?: JSONObject.NULL)
            .put("coverPhoto", p.coverPhoto ?: JSONObject.NULL)
            .put("avisoNuevos", p.avisoNuevos)
            .put("avisoAniversarios", p.avisoAniversarios)
            .put("avisoVoz", p.avisoVoz)
            .put("avisoUnDiaComoHoy", p.avisoUnDiaComoHoy)
        prefs.edit().putString("profile", o.toString()).apply()
    }

    fun loadPlaces() = loadList("places") {
        Place(it.getString("id"), it.getString("name"), it.getDouble("lon"), it.getDouble("lat"), it.optBoolean("visited"), it.optString("date"))
    }

    fun savePlaces(items: List<Place>) = saveList("places", items) {
        JSONObject().put("id", it.id).put("name", it.name).put("lon", it.lon).put("lat", it.lat)
            .put("visited", it.visited).put("date", it.date)
    }

    fun loadMemories() = loadList("memories") {
        Memory(
            it.getLong("id"), it.getString("placeId"), MemoryKind.valueOf(it.getString("kind")), it.optString("label"),
            it.optString("msg"), it.optString("by"), LocalDate.parse(it.getString("date")), it.optStringOrNull("uri"), it.optInt("secs"),
        )
    }

    fun saveMemories(items: List<Memory>) = saveList("memories", items) {
        JSONObject().put("id", it.id).put("placeId", it.placeId).put("kind", it.kind.name).put("label", it.label)
            .put("msg", it.msg).put("by", it.by).put("date", it.date.toString()).put("uri", it.uri ?: JSONObject.NULL).put("secs", it.secs)
    }

    fun loadPoems() = loadList("poems") { o ->
        val a = o.optJSONArray("audios") ?: JSONArray()
        val audios = (0 until a.length()).map { i ->
            val r = a.getJSONObject(i)
            Recitation(r.getString("by"), r.getInt("secs"), r.optStringOrNull("uri"))
        }
        Poem(o.getLong("id"), o.getString("title"), o.getString("text"), o.getString("by"), LocalDate.parse(o.getString("date")), audios)
    }

    fun savePoems(items: List<Poem>) = saveList("poems", items) { p ->
        val audios = JSONArray()
        p.audios.forEach { audios.put(JSONObject().put("by", it.by).put("secs", it.secs).put("uri", it.uri ?: JSONObject.NULL)) }
        JSONObject().put("id", p.id).put("title", p.title).put("text", p.text).put("by", p.by)
            .put("date", p.date.toString()).put("audios", audios)
    }

    fun loadIdeas() = loadList("ideas") { Idea(it.getLong("id"), it.getString("text"), it.getString("by"), it.optBoolean("done")) }

    fun saveIdeas(items: List<Idea>) = saveList("ideas", items) {
        JSONObject().put("id", it.id).put("text", it.text).put("by", it.by).put("done", it.done)
    }

    fun loadNotices() = loadList("notices") {
        Notice(
            it.getLong("id"), it.getString("who"), it.getString("text"), it.getString("time"),
            it.optStringOrNull("img"), Destino.valueOf(it.getString("go")), it.optBoolean("unread"),
        )
    }

    fun saveNotices(items: List<Notice>) = saveList("notices", items) {
        JSONObject().put("id", it.id).put("who", it.who).put("text", it.text).put("time", it.time)
            .put("img", it.img ?: JSONObject.NULL).put("go", it.go.name).put("unread", it.unread)
    }

    private fun <T> loadList(key: String, parse: (JSONObject) -> T): List<T>? {
        val raw = prefs.getString(key, null) ?: return null
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { parse(array.getJSONObject(it)) }
        }.getOrNull()
    }

    private fun <T> saveList(key: String, items: List<T>, toJson: (T) -> JSONObject) {
        val array = JSONArray()
        items.forEach { array.put(toJson(it)) }
        prefs.edit().putString(key, array.toString()).apply()
    }

    private fun JSONObject.optStringOrNull(key: String): String? =
        if (!has(key) || isNull(key)) null else optString(key).ifEmpty { null }
}
