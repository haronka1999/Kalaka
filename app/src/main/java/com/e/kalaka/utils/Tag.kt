package com.e.kalaka.utils


class Tag {
    companion object {
        private val tags: List<Pair<String, String>> =
            listOf(Pair("ic_necklace", "Jewelry"),
                    Pair("ic_bag", "Bags"),
                    Pair("ic_cup",  "Cups"),
                    Pair("ic_shirt", "T-shirts"))
        fun getTags(): List<Pair<String, String>> = tags
        fun tagCount(): Int = tags.size
    }
}