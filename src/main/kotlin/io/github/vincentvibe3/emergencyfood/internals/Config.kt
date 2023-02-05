package io.github.vincentvibe3.emergencyfood.internals

object Config {

    lateinit var channel: ConfigLoader.Channel
    lateinit var token: String
    lateinit var owner: String
    var exclusions = listOf<String>()
    var prefix = "$"
    var status = "Now using Slash Commands"
    var testServer = ""
    var logflareUrl = ""
    var logflareKey = ""
    var envName = "unknown"
    var supabaseUrl = ""
    var supabaseKey = ""

}