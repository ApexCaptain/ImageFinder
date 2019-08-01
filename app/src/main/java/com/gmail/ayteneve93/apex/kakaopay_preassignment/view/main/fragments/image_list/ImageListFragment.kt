package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list

import android.content.res.Configuration
import android.graphics.Rect
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.FragmentImageListBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseFragment
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler.ImageListRecyclerAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ImageListFragment : BaseFragment<FragmentImageListBinding, ImageListViewModel>() {

    private val mImageListViewModel : ImageListViewModel by viewModel()
    private val mImageListRecyclerAdapter :ImageListRecyclerAdapter by inject()

    override fun getLayoutId(): Int {
        return R.layout.fragment_image_list
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): ImageListViewModel {
        return mImageListViewModel
    }

    override fun setUp() {
        

        setImageListRecyclerAdapter()
    }

    private fun setImageListRecyclerAdapter() {
       mViewDataBinding.imageListRecyclerView.apply {
           adapter = mImageListRecyclerAdapter
           layoutManager =
               if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(mActivity, 3)
               else GridLayoutManager(mActivity, 5)
           addItemDecoration(object : RecyclerView.ItemDecoration() {
               override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                   val space = 15
                   outRect.apply {
                       left = space
                       right = space
                       bottom = space
                       top = if(parent.getChildLayoutPosition(view) == 0) space else 0
                   }
               }
           })
       }



        mImageListRecyclerAdapter.searchImage("설현", KakaoImageSortOption.ACCURACY, 1, 45)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mViewDataBinding.imageListRecyclerView.layoutManager =
            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(mActivity, 3)
            else GridLayoutManager(mActivity, 5)
    }
}
