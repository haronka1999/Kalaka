package com.e.kalaka.utils


class Tag {
    companion object {
        private val tags: List<Pair<String, String>> =
            listOf(Pair("ic_necklace", "Ékszerek"),
                    Pair("ic_bag", "Szatyrok"),
                    Pair("ic_cup",  "Bögrék"),
                    Pair("ic_shirt", "Pólók"),
                    Pair("ic_candle", "Gyertyák"),
                    Pair("ic_wreath", "Koszorúk"),
                    Pair("ic_dog_house", "Kutyaházak"),
                    Pair("ic_decoration", "Dekoráció"),
                    Pair("ic_christmas_decoration", "Karácsonyi díszek"),
                    Pair("ic_gift", "Ajándéktárgyak"),
                    Pair("ic_painting", "Festmények"),
                    Pair("ic_pillows", "Párnák"),
                    Pair("ic_napkin", "Szalvéták"),
                    Pair("ic_cap", "Kalapok"),
                    Pair("ic_pottery", "Kerámiák"),
                    Pair("ic_lamp", "Lámpák")
            )
        fun getTags(): List<Pair<String, String>> = tags
        fun tagCount(): Int = tags.size
        fun getTagList(): ArrayList<String> {
            val array = arrayListOf<String>()
            tags.forEach{
                array.add(it.second)
            }
            return array
        }
    }
}