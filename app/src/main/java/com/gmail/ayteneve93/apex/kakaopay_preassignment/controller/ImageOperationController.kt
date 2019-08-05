package com.gmail.ayteneve93.apex.kakaopay_preassignment.controller

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.ObservableField
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.gmail.ayteneve93.apex.kakaopay_preassignment.BuildConfig
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainBroadcastPreference
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ImageOperationController(
    private val application: Application
) {
    private val mDownloadDirectory : File by lazy {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).also {
            if(!it.exists()) it.mkdir()
        }
    }
    private val mShareDirectory : File by lazy {
        File(application.filesDir.canonicalPath + "/sharedImages").also {
            if(!it.exists()) it.mkdir()
        }
    }
    private val mImageModelMap : HashMap<String, KakaoImageModel> = HashMap()
    private val mCompositeDisposable : CompositeDisposable = CompositeDisposable()
    var mIsOnOperation = ObservableField(false)
    private var mIsImageOnSharing = false
    fun isImageModelExists(imageModel: KakaoImageModel) : Boolean = mImageModelMap.containsKey(imageModel.imageUrl)
    fun addImageModel(imageModel: KakaoImageModel) { mImageModelMap.put(imageModel.imageUrl, imageModel) }
    fun removeImageModel(imageModel: KakaoImageModel) = mImageModelMap.remove(imageModel.imageUrl)
    fun clearImageModels() = mImageModelMap.clear()

    private enum class ImageOperation{ SHARE, DOWNLOAD }

    fun startShare() {
        checkPermsiionAnd(ImageOperation.SHARE)
    }
    fun clearSharedDriectory() {
        if(mIsImageOnSharing) {
            mIsImageOnSharing = false
            mShareDirectory.listFiles().forEach {
                if (it.exists()) it.canonicalFile.delete()
            }
            mCompositeDisposable.clear()
        }
    }

    fun startDownload() {
        checkPermsiionAnd(ImageOperation.DOWNLOAD)
    }

    private fun checkPermsiionAnd(imageOperation: ImageOperation) {
        val clonedImageModelMap : HashMap<String, KakaoImageModel> = with(mImageModelMap) {
            val tmpHashMap = HashMap<String, KakaoImageModel>()
            forEach { tmpHashMap[it.key] = it.value }
            tmpHashMap
        }
        TedPermission.with(application)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    val isWifiConnected : Boolean = with((application.getSystemService(Context.WIFI_SERVICE) as WifiManager)) {
                        isWifiEnabled && connectionInfo.networkId != -1
                    }
                    if(isWifiConnected) loadImageTo(clonedImageModelMap, imageOperation)
                    else {
                        AlertDialog.Builder(application).apply {
                            setTitle(R.string.dialog_image_download_wifi_warning_title)
                            setMessage(R.string.dialog_image_download_wifi_warning_message)
                            setPositiveButton(R.string.dialog_image_download_wifi_warning_positive_button_txt) { _, _ ->
                                loadImageTo(clonedImageModelMap, imageOperation)
                            }
                            setNegativeButton(R.string.dialog_image_download_wifi_warning_negative_button_txt) { _, _ ->
                                preProcessRejected()
                            }
                        }
                    }
                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                    preProcessRejected()
                }

                private fun preProcessRejected() {
                    Toast.makeText(application, R.string.txt_image_download_failed, Toast.LENGTH_LONG).show()
                }

            })
            .setRationaleMessage(R.string.permission_external_storage_rational_message)
            .setDeniedMessage(R.string.permission_external_storage_denied_message)
            .setGotoSettingButton(true)
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    private fun loadImageTo(clonedImageModelMap : HashMap<String, KakaoImageModel>, imageOperation : ImageOperation) {
        val totalImageCount = clonedImageModelMap.size
        var currentImageCount = 0
        val directoryToStore = if(imageOperation == ImageOperation.SHARE)mShareDirectory else mDownloadDirectory
        mIsOnOperation.set(true)
        mCompositeDisposable.add(
            Completable.create {
                emitter ->
                clonedImageModelMap.forEach {
                    Glide.with(application)
                        .asBitmap()
                        .load(it.value.imageUrl)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                currentImageCount++
                                if(currentImageCount == totalImageCount) emitter.onComplete()
                            }
                            override fun onLoadCleared(placeholder: Drawable?) = Unit
                            override fun onResourceReady(bitmapImage: Bitmap, transition: Transition<in Bitmap>?) {
                                val imageFile = File(directoryToStore, application.getString(R.string.download_image_prefix, it.value.hashCode()) + ".jpg")
                                try {
                                    FileOutputStream(imageFile).also { fileOutputStream ->
                                        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                                        fileOutputStream.close()
                                        notifyAndroidNewImageAdded(imageFile)
                                    }
                                } catch(e : Exception) { e.printStackTrace() }
                                currentImageCount++
                                if(currentImageCount == totalImageCount) emitter.onComplete()
                            }
                        })
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mIsOnOperation.set(false)
                application.sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.IMAGE_OPERATION_FINISHED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.MAIN_ACTIVITY)
                    when(imageOperation) {
                        ImageOperation.SHARE -> {
                            putExtra(MainBroadcastPreference.Extra.ImageOperation.KEY, MainBroadcastPreference.Extra.ImageOperation.PredefinedValues.SHARE)
                            putExtra(Intent.EXTRA_INTENT, Intent().apply {
                                action = Intent.ACTION_SEND_MULTIPLE
                                type = "image/jpeg"
                                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList<Uri>().also {
                                    mShareDirectory.listFiles().forEach { eachFileToShare ->
                                        if(eachFileToShare.extension == "jpg") it.add(FileProvider.getUriForFile(application, BuildConfig.APPLICATION_ID, eachFileToShare))
                                    }
                                })
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                            mIsImageOnSharing = true
                        }
                        ImageOperation.DOWNLOAD -> {
                            putExtra(MainBroadcastPreference.Extra.ImageOperation.KEY, MainBroadcastPreference.Extra.ImageOperation.PredefinedValues.DOWNLOAD)
                        }
                    }
                })
            }
        )
    }

    private fun notifyAndroidNewImageAdded(imageFile : File) {
        application.sendBroadcast(Intent().apply {
            action = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
            data = Uri.fromFile(imageFile)
        })
    }

}
























