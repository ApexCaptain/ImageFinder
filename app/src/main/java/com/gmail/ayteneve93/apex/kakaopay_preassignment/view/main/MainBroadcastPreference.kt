package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
object MainBroadcastPreference {

    object Action {
        const val NEW_SEARCH_QUERY_INPUT = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.NEW_SEARCH_QUERY_INPUT"
        const val SORT_OPTION_CHANGED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.SORT_OPTION_CHANGED"
        const val DISPLAY_COUNT_CHANGED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.DISPLAY_COUNT_CHANGED"
        const val PINCHING = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.PINCHING"
        const val PINCH_STATE = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.PINCH_STATE"
        const val IMAGE_ITEM_CLICKED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.IMAGE_ITEM_CLICKED"
        const val BACK_BUTTON_PRESSED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.BACK_BUTTON_PRESSED"
        const val CLOSE_IMAGE_DETAIL_FRAGMENT = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.CLOSE_IMAGE_DETAIL_FRAGMENT"
        const val IMAGE_ITEM_SELECTION_MODE_CHANGED = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED"
    }

    object Target {
        const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Target.KEY"
        object PredefinedValues {
            const val MAIN_ACTIVITY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Target.PredefinedValues.MAIN_ACTIVITY"
            const val IMAGE_LIST = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Target.PredefinedValues.IMAGE_LIST"
            const val IMAGE_DETAIL = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Target.PredefinedValues.IMAGE_DETAIL"
        }
    }

    object Extra {
        object QueryString {
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.QueryString.KEY"
        }
        object SortOption {
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.SortOption.KEY"
        }
        object IsZoomIn {
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.IsZoomIn.KEY"
            object PredefinedValues {
                const val ZOOM_IN = true
                const val ZOOM_OUT = false
            }
        }
        object IsPichBeigin {
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.IsPichBeigin.KEY"
            object PredefinedValues {
                const val BEGIN = true
                const val END = false
            }
        }
        object DisplayCount {
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.DisplayCount.KEY"
        }
        object ImageItem {
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.ImageItem.KEY"
        }
        object ImageItemSelectionMode {
            const val KEY = "com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.Extra.ImageItemSelectionMode.KEY"
            object PredefinedValues {
                const val SELECTION_MODE = true
                const val NORMAL_MODE = false
            }
        }
    }

}