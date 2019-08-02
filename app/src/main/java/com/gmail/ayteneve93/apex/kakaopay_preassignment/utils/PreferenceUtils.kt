package com.gmail.ayteneve93.apex.kakaopay_preassignment.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption

private const val USER_PREF_KEY = "utils.User.KEY"
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class PreferenceUtils(
    application: Application
) {
    private val userPreference : SharedPreferences = application.getSharedPreferences(USER_PREF_KEY, Context.MODE_PRIVATE)

    fun getSortOption() : KakaoImageSortOption =
        KakaoImageSortOption.getSortOptionFromString(userPreference.getString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName, KakaoImageSortOption.ACCURACY.optionString)!!)
    fun setSortOption(sortOption : KakaoImageSortOption) =
        userPreference.edit().putString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName, sortOption.optionString).apply()

    fun getDisplayCount() : Int =
        userPreference.getInt(PreferenceCategory.User.DISPLAY_COUNT.attributeName, 30)
    fun setDisplayCount(displayCount : Int) =
        userPreference.edit().putInt(PreferenceCategory.User.DISPLAY_COUNT.attributeName, displayCount).apply()

    fun getImageSizePercentage() : Float =
        userPreference.getFloat(PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE.attributeName, 1.0f)
    fun setImageSizePercentage(imageSizePercentage : Float) =
        userPreference.edit().putFloat(PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE.attributeName, imageSizePercentage).apply()

    fun getImageColumnCount() : Int =
        userPreference.getInt(PreferenceCategory.User.IMAGE_COLUMN_COUNT.attributeName, 3)
    fun setImageColumnCount(imageColumnCount : Int) =
        userPreference.edit().putInt(PreferenceCategory.User.IMAGE_COLUMN_COUNT.attributeName, imageColumnCount).apply()

}

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
object PreferenceCategory {
    enum class User(val attributeName : String) {
        KAKAO_IMAGE_SORT_OPTION("utils.PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION"),
        DISPLAY_COUNT("utils.PreferenceCategory.User.DISPLAY_COUNT"),
        IMAGE_SIZE_PERCENTAGE("utils.PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE"),
        IMAGE_COLUMN_COUNT("utils.PreferenceCategory.User.IMAGE_COLUMN_COUNT")
    }
}