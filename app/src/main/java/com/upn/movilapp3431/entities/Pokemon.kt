package com.upn.movilapp3431.entities

class Pokemon ( val name : String , val url: String )
class PokemonRes (val count : Int, val  next : String , val previous :String? , val results : List<Pokemon>)

