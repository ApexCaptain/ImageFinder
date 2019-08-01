package com.gmail.ayteneve93.apex.kakaopay_preassignment.data

import java.time.LocalDate
import java.util.*

@Suppress("spellCheckingInspection")
data class KakaoImageModel(
    val collection : String,
    val date : LocalDate,
    val displaySitename : String,
    val docUrl : String,
    val height : Int,
    val imageUrl : String,
    val thumbnailUrl : String,
    val width : Int
)

@Suppress("spellCheckingInspection")
data class KakaoImageModelList(
    val isEnd : Boolean,
    val pageableCount : Int,
    val totalCount : Int,
    val documents : ArrayList<KakaoImageModel>
)