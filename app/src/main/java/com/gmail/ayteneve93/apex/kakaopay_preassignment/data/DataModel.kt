package com.gmail.ayteneve93.apex.kakaopay_preassignment.data

import java.time.LocalDate
import java.util.*

@Suppress("spellCheckingInspection")
data class KakaoImageModel(
    val collection : String,
    val datetime : LocalDate,
    val display_sitename : String,
    val doc_url : String,
    val height : Int,
    val image_url : String,
    val thumbnail_url : String,
    val width : Int
)

@Suppress("spellCheckingInspection")
data class KakaoImageModelList(
    val is_end : Boolean,
    val pageable_count : Int,
    val total_count : Int,
    val documents : ArrayList<KakaoImageModel>
)