package com.e.kalaka.utils


class Tag {
    companion object {
        private val tags: List<Pair<String, String>> =
            listOf(Pair("ic_necklace", "Ékszerek"),
                    Pair("ic_bag", "Szatyrok"),
                    Pair("ic_cup",  "Bögrék"),
                    Pair("ic_shirt", "Pólók"))
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