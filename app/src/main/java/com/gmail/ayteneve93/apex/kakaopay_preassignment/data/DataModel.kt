package com.gmail.ayteneve93.apex.kakaopay_preassignment.data

import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import java.io.Serializable
import java.time.LocalDate
import java.util.*

/**
 * 카카오 Image Api 에서 가져온 각각의 이미지 정보들을 담는 데이터 모델입니다.
 *
 * @property collection 이미지 수집경로입니다.
 * @property date 해당 Document의 작성 일자입니다.
 * @property displaySitename 표시되는 사이트 이름입니다.
 * @property docUrl Documnet 의 Url 입니다.
 * @property height 이미지의 높이입니다.
 * @property imageUrl 이미지의 Url 입니다.
 * @property thumbnailUrl Thumbnail 의 Url 입니다.
 * @property width 이미지의 폭입니다.
 *
 * @author ayteneve93@gmail.com
 *
 * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSearchApi
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
data class KakaoImageModel(
    val collection : String,
    val date : LocalDate,
    val displaySitename : String,
    val docUrl : String,
    val height : Int,
    val imageUrl : String,
    val thumbnailUrl : String,
    val width : Int
) : Serializable

/**
 * 카카오 Image Api 에서 가져온 이미지 모델 전체를 담는데 사용하는 데이터 모델입니다.
 *
 * @property isEnd 현재 페이지가 마지막 페이지인지 여부 입니다.
 * @property pageableCount totalCount 중에 노출 가능한 문서의 수 입니다.
 * @property totalCount 검색어에 검색된 문서 수 입니다.
 * @property documents KakaoImageModel 이 저장될 리스트입니다.
 *
 * @author ayteneve93@gmail.com
 *
 * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSearchApi
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
data class KakaoImageModelList(
    val isEnd : Boolean,
    val pageableCount : Int,
    val totalCount : Int,
    val documents : ArrayList<KakaoImageModel>
)