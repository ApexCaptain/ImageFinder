package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils

/**
 * MainActivity 와 예하 프래그먼트간의 Broadcast 통신 규약을 지정하는 Object 입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
object MainBroadcastPreference {

    /** 방송 액션 정보입니다. */
    object Action {
        /** 새로운 검색어가 유입된 경우 */
        const val NEW_SEARCH_QUERY_INPUT = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.NEW_SEARCH_QUERY_INPUT"
        /** 정렬 기준이 변경된 경우 */
        const val SORT_OPTION_CHANGED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.SORT_OPTION_CHANGED"
        /** 표시 이미지 갯수가 변경된 경우 */
        const val DISPLAY_COUNT_CHANGED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.DISPLAY_COUNT_CHANGED"
        /** 화면 확대 혹은 축소 이벤트가 발생한 경우 */
        const val PINCHING = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.PINCHING"
        /** 화면 확대 혹은 축소 이벤트가 시작 혹은 종료된 경우 */
        const val PINCH_STATE = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.PINCH_STATE"
        /** 특정 이미지 아이템이 클릭된 경우 */
        const val IMAGE_ITEM_CLICKED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.IMAGE_ITEM_CLICKED"
        /** 뒤로가기 버튼이 눌린 경우 */
        const val BACK_BUTTON_PRESSED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.BACK_BUTTON_PRESSED"
        /** ImageDetail Fragment 각 닫힌 경우 */
        const val CLOSE_IMAGE_DETAIL_FRAGMENT = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.CLOSE_IMAGE_DETAIL_FRAGMENT"
        /** 아이템 선택 모드가 단일 혹은 다중 선택 모드 등으로 변경되는 경우 */
        const val IMAGE_ITEM_SELECTION_MODE_CHANGED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED"
        /** 저장/다운로드 기능이 종료된 경우 */
        const val IMAGE_OPERATION_FINISHED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.IMAGE_OPERATION_FINISHED"
        /** 애플리케이션을 종료하라는 명령인 경우 */
        const val FINISH_APPLICATION = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.FINISH_APPLICATION"
        /** 와이파이가 연결되지 않은 상태에서의 이미지 작업 수행 여부 확인 */
        const val CHECK_IMAGE_OPERATION_PROCEEDING_WHEN_WIFI_DISCONNECTED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.CHECK_IMAGE_OPERATION_PROCEEDING_WHEN_WIFI_DISCONNECTED"
    }

    /** 방송 수신 대상자 정보입니다. */
    object Target {
        /** 방송 수신 대상자의 키 값 */
        const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Target.KEY"
        /** 미리 정의된 값 목록 */
        object PreDefinedValues {
            /** 매인 액티비티 */
            const val MAIN_ACTIVITY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Target.PreDefinedValues.MAIN_ACTIVITY"
            /** 이미지 리스트 프래그먼트 */
            const val IMAGE_LIST = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Target.PreDefinedValues.IMAGE_LIST"
            /** 이미지 상세정보 프래그먼트 */
            const val IMAGE_DETAIL = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Target.PreDefinedValues.IMAGE_DETAIL"
        }
    }

    /** 기타 추가 정보 */
    object Extra {
        /** 새롭게 입력된 검색어 */
        object QueryString {
            /** 새롭게 입력된 검색어 키 값 */
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.QueryString.KEY"
        }
        /** 변경된 정렬 기준 */
        object SortOption {
            /** 변경된 정렬 기준 키 값 */
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.SortOption.KEY"
        }
        /** Pinch 정보 */
        object PinchingOperation {
            /** Pinch 정보 키 값 */
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.PinchingOperation.KEY"
            /** 미리 정의된 값 목록 */
            enum class PreDefinedValues {
                /** 줌 인 */
                ZOOM_IN,
                /** 줌 아웃 */
                ZOOM_OUT
            }
        }
        /** Pinch 시작/종료 정보 */
        object PinchingState {
            /** Pinch 시작/종료 정보 키 값*/
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.PinchingState.KEY"
            /** 미리 정의된 값 목록 */
            enum class PreDefinedValues {
                /** Picn 시작 */
                PINCH_START,
                /** Pinch 종료 */
                PINCH_END,
            }
        }
        /** 변경된 이미지 표시 갯수 */
        object DisplayCount {
            /** 변경된 이미지 표시 갯수 키 값 */
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.DisplayCount.KEY"
        }
        /** 선택된 이미지 아이템 */
        object ImageItem {
            /** 선택된 이미지 아이템 키 값 */
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.ImageItem.KEY"
        }
        /** 변경된 이미지 선택 모드 */
        object ImageItemSelectionMode {
            /** 변경된 이미지 선택 모드 키 값 */
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.ImageItemSelectionMode.KEY"
            /** 미리 정의된 값 목록 */
            enum class PreDefinedValues {
                /** 다중 선택 모드 */
                MULTI_SELECTION_MODE,
                /** 일반 단일 선택 모드 */
                SIGNLE_SELECTION_MODE,
            }
        }
        /** 이미지 다운로드/공유 정보 */
        object ImageOperation {
            /** 이미지 다운로드/공유 정보 키 값 */
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.ImageOperation.KEY"
            /** 미리 정의된 값 목록 */
            enum class PreDefinedValues {
                /** 이미지 공유 */
                SHARE,
                /** 이미지 다운로드 */
                DOWNLOAD
            }
        }
    }

}