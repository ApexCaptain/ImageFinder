package com.gmail.ayteneve93.apex.kakaopay_preassignment.controller

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.ArrayList

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ImageDownloadController(
    private val application: Application
) {

    private val mDownloadDirectory : File by lazy {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).also {
            if(!it.exists()) it.mkdir()
        }
    }

    private val mImageModelMap : HashMap<String, KakaoImageModel> = HashMap()
    fun isImageModelExists(imageModel: KakaoImageModel) : Boolean = mImageModelMap.containsKey(imageModel.imageUrl)
    fun addImageModel(imageModel: KakaoImageModel) = mImageModelMap.put(imageModel.imageUrl, imageModel)
    fun removeImageModel(imageModel: KakaoImageModel) = mImageModelMap.remove(imageModel.imageUrl)
    fun clearImageModels() = mImageModelMap.clear()


    fun test(kakaoImageModel: KakaoImageModel) {
        Glide.with(application)
            .asBitmap()
            .load(kakaoImageModel.imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) = Unit
                override fun onResourceReady(image: Bitmap, transition: Transition<in Bitmap>?) {

                    saveImage(image, kakaoImageModel)
                }
            })
    }

    private fun saveImage(image : Bitmap, imageModel : KakaoImageModel) {
        TedPermission.with(application)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    val imageFile = File(mDownloadDirectory, application.getString(R.string.download_image_prefix, imageModel.hashCode()) + ".jpg")
                    try {
                        FileOutputStream(imageFile).also {
                            image.compress(Bitmap.CompressFormat.JPEG, 100, it)
                            it.close()
                            notifyAndroidNewImageAdded(imageFile)
                        }
                    } catch (e : Exception) { e.printStackTrace() }
                }
                override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                }
            })
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()

    }

    private fun notifyAndroidNewImageAdded(imageFile : File) {
        application.sendBroadcast(Intent().apply {
            action = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
            data = Uri.fromFile(imageFile)
        })
    }


}
