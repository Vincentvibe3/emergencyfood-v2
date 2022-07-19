package io.github.vincentvibe3.emergencyfood.utils.supabase

class SupabaseFilter(
    val key:String,
    val value:String,
    val matchType: Match
) {
    enum class Match{
        EQUALS
    }

    override fun toString(): String {
        if (matchType==Match.EQUALS){
            return "$key=eq.$value"
        }
        return "$key=eq.$value"
    }
}