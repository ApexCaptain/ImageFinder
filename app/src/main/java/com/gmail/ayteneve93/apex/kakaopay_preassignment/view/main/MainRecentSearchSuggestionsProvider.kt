package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import android.content.SearchRecentSuggestionsProvider

class MainRecentSearchSuggestionsProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }
    companion object {
        const val AUTHORITY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainRecentSearchSuggestionsProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }
}