package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import android.content.SearchRecentSuggestionsProvider

/**
 * MainActivity 의 ViewModel 입니다.
 *
 * @property AUTHORITY 프로바이더의 Authority 명입니다.
 * @property MODE 프로바이더의 데이터 제공 방식입니다. 이 경우 한 번에 1개의 String 데이터만 제공하므로 DATABASE_MODE_QUERIES 로 지정했습니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
class MainRecentSearchSuggestionsProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }
    companion object {
        const val AUTHORITY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainRecentSearchSuggestionsProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }
}