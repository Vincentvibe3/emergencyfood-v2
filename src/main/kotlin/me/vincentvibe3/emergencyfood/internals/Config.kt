package me.vincentvibe3.emergencyfood.internals

object Config {

    lateinit var channel: ConfigLoader.Channel
    lateinit var token:String
    lateinit var owner:String
    var exclusions = ArrayList<String>()

}