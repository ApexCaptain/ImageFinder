package com.gmail.ayteneve93.apex.kakaopay_preassignment.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption

private const val USER_PREF_KEY = "utils.User.KEY"
/**
 * SharedPreference 에 사용자의 최신 설정 내용을 저장하는 Utility 입니다.
 *
 * @property userPreference 사용자 환경설정 정보를 담는 SharedPreference 객체입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class PreferenceUtils(
    /** DI를 통해 받아오는 Constructor Field 입니다. SharedPreference 객체를 가져오는데 사용합니다. */
    application: Application
) {

    private val userPreference : SharedPreferences = application.getSharedPreferences(USER_PREF_KEY, Context.MODE_PRIVATE)

    /**
     * 최근 사용자가 사용한 사진 정렬 옵션을 가져옵니다.
     *
     * @return 최근 사용한 정렬 옵션입니다. 기본값은 ACCURACY, '최신순'입니다.
     *
     * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
     */
    fun getSortOption() : KakaoImageSortOption =
        KakaoImageSortOption.getSortOptionFromString(userPreference.getString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName, KakaoImageSortOption.ACCURACY.optionString)!!)
    /**
     * 사용자의 정렬 옵션을 저장합니다.
     *
     * @param sortOption 저장할 정렬 옵션입니다.
     *
     * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
     */
    fun setSortOption(sortOption : KakaoImageSortOption) =
        userPreference.edit().putString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName, sortOption.optionString).apply()


    /**
     * 최근 사용자가 사용한 페이지당 사진 표시 갯수를 가져옵니다.
     *
     * @return 최근 사용한 페이지당 사진 표시 갯수입니다. 기본값은 30개 입니다.
     */
    fun getDisplayCount() : Int =
        userPreference.getInt(PreferenceCategory.User.DISPLAY_COUNT.attributeName, 30)

    /**
     * 페이지당 사진 표시 갯수를 저장합니다.
     *
     * @param displayCount 저장할 사진 표시 갯수
     */
    fun setDisplayCount(displayCount : Int) =
        userPreference.edit().putInt(PreferenceCategory.User.DISPLAY_COUNT.attributeName, displayCount).apply()


    /**
     * 최근 사용자가 사용한 이미지 확대 수치를 가져옵니다.
     *
     * @return 최근 사용자가 사용한 이미지 확대 수치입니다. 기본값은 1.0, 100%입니다.
     */
    fun getImageSizePercentage() : Float =
        userPreference.getFloat(PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE.attributeName, 1.0f)

    /**
     * 이미지 확대 수치를 저장합니다.
     *
     * @param imageSizePercentage 저장할 이미지 확대 수치입니다.
     */
    fun setImageSizePercentage(imageSizePercentage : Float) =
        userPreference.edit().putFloat(PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE.attributeName, imageSizePercentage).apply()


    /**
     * 최근 사용자가 사용한 이미지를 표시하는데 사용되는 열의 갯수입니다.
     *
     * @return 최근 사용자가 사용한 이미지 표시 열 갯수입니다. 기본값은 3입니다.
     */
    fun getImageColumnCount() : Int =
        userPreference.getInt(PreferenceCategory.User.IMAGE_COLUMN_COUNT.attributeName, 3)

    /**
     * 이미지 표시 열의 갯수를 저장합니다.
     *
     * @param imageColumnCount 저장할 이미지 표시 열 갯수입니다.
     */
    fun setImageColumnCount(imageColumnCount : Int) =
        userPreference.edit().putInt(PreferenceCategory.User.IMAGE_COLUMN_COUNT.attributeName, imageColumnCount).apply()

}

/**
 * 저장 정보 범주입니다.
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
private object PreferenceCategory {
    /** 유저와 관련된 환경설정 정보 범주입니다. */
    enum class User(val attributeName : String) {
        /** 이미지 정렬 기준 */
        KAKAO_IMAGE_SORT_OPTION("utils.PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION"),
        /** 페이지당 이미지 표시 갯수 */
        DISPLAY_COUNT("utils.PreferenceCategory.User.DISPLAY_COUNT"),
        /** 이미지 확대 수치 */
        IMAGE_SIZE_PERCENTAGE("utils.PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE"),
        /** 이미지를 표시하는 Grid 의 열 갯수 */
        IMAGE_COLUMN_COUNT("utils.PreferenceCategory.User.IMAGE_COLUMN_COUNT")
    }
}