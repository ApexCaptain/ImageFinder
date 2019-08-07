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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList

/**
 * Glide 를 활용해서 이미지 URL 을 바탕으로 이미지 파일을 참조, 압축하고
 * 안드로이드 기기의 Download 폴더에 저장하거나 다른 사람에게 공유할 수 있는 기능을 지원합니다.
 * DI를 통해 관리되는 SingleTon 클래스입니다.
 *
 * @property application DI를 통해 받아오는 Constructor Field 입니다. sendBroadcast 메소드를 위해 사용합니다.
 * @property mDownloadDirectory 이미지 파일이 다운로드 될 외장 디렉토리입니다.
 * @property mShareDirectory 이미지 파일을 외부 App과 공유하기 위해 임시로 저장하는 디렉토리입니다.
 * @property mImageModelMap 다운로드 혹은 공유할 이미지 모델들을 임시 저장해두는 Map 객체입니다.
 * @property mCompositeDisposable Rx 작업 종료 후 Disposable 객체를 모아두었다 한 번에 처리하기 위한 CompositeDisposable 입니다.
 * @property mIsOnOperation 이미지 다운로드/압축 작업이 진행중임을 알리는 Observable Boolean 객체입니다.
 * @property mIsImageOnSharing 이미지 공유 작업이 진행중임을 알리는 Boolean 객체입니다.
 *
 * @author ayteneve93@gmail.com
 *
 * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
 */
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

    /** Operation Enum 으로 공유와 다운로드가 있습니다. */
    private enum class ImageOperation{
        /** 공유 */
        SHARE,
        /** 다운로드 */
        DOWNLOAD
    }

    /**
     * 이미지 모델을 추가하기 전에 이미 등록되어있는지 확인하는 메소드입니다.
     *
     * @param imageModel 존재 여부를 확인할 이미지 모델입니다.
     * @return 해당 이미지 모델이 이미 등록되어있는지 여부입니다.
     *
     * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
     */
    fun isImageModelExists(imageModel: KakaoImageModel) : Boolean = mImageModelMap.containsKey(imageModel.imageUrl)
    /**
     * 이미지 모델을 추가하는 메소드입니다.
     *
     * @param imageModel 추가할 이미지 모델입니다 ,
     *
     * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
     */
    fun addImageModel(imageModel: KakaoImageModel) = mImageModelMap.put(imageModel.imageUrl, imageModel)
    /**
     * 이미지 모델을 제거하는 메소드입니다.
     *
     * @param imageModel 제거할 이미지 모델입니다,
     *
     * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
     */
    fun removeImageModel(imageModel: KakaoImageModel) = mImageModelMap.remove(imageModel.imageUrl)
    /** 추가된 모든 이미지 모델을 제거하는 메소드입니다. */
    fun clearImageModels() = mImageModelMap.clear()

    /** 공유 절차를 시작합니다. */
    fun startShare() = checkPermsiionAndLoadImagesForOperation(ImageOperation.SHARE)
    /** 다운로드 절차를 시작합니다. */
    fun startDownload() = checkPermsiionAndLoadImagesForOperation(ImageOperation.DOWNLOAD)
    /** 임시 공유 파일들을 제거하고 compositeDisposable 에 입력된 Disposable 들을 모두 제거합니다. */
    fun clearSharedDriectory() {
        if(mIsImageOnSharing) {
            mIsImageOnSharing = false
            mShareDirectory.listFiles().forEach {
                if (it.exists()) it.canonicalFile.delete()
            }
            mCompositeDisposable.clear()
        }
    }
    /** compositeDisposable 에 입력된 Disposable 들을 모두 제거합니다. */
    fun clearDisposable() = mCompositeDisposable.clear()



    // 이하 Private 메소드
    /**
     * 이미지 로딩 작업을 시작하기 전 TedPermission 으로 Storage 권한 획득여부를 점검합니다.
     *
     * @param imageOperation 이미지를 공유(SHARE)할지  다운로드(DOWNLOAD) 할지 확인합니다,
     */
    private fun checkPermsiionAndLoadImagesForOperation(imageOperation: ImageOperation) {
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
                    Toast.makeText(application, R.string.txt_image_operation_failed, Toast.LENGTH_LONG).show()
                }

            })
            .setRationaleMessage(R.string.permission_external_storage_rational_message)
            .setDeniedMessage(R.string.permission_external_storage_denied_message)
            .setGotoSettingButton(true)
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    /**
     * Rx Completable 에 Glide 프로세스를 등록해서 사용합니다. 이미지 모델에서 imageUrl 을 추출하여
     * 네트워크에서 이미지 Resource 를 추출하고(Bitmap) 이를 .jpg 로 압축 후 저장 혹은 공유합니다.
     *
     * @param clonedImageModelMap 이미지 모델들이 저장된 map 객체의 사본입니다.
     * @param imageOperation 추출/압축 한 이미지들을 저장할지 공유할지 판단합니다.
     */
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
                                        if(imageOperation == ImageOperation.DOWNLOAD) notifyAndroidNewImageAdded(imageFile)
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
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY)
                    when(imageOperation) {
                        ImageOperation.SHARE -> {
                            putExtra(MainBroadcastPreference.Extra.ImageOperation.KEY, MainBroadcastPreference.Extra.ImageOperation.PreDefinedValues.SHARE)
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
                            putExtra(MainBroadcastPreference.Extra.ImageOperation.KEY, MainBroadcastPreference.Extra.ImageOperation.PreDefinedValues.DOWNLOAD)
                        }
                    }
                })
            }
        )
    }

    /**
     * 외장 메모리에 이미지가 저장되었다면 안드로이드 시스템에 새로운 파일이
     * 추가되었음을 알립니다.
     *
     * @param imageFile 추가된 이미지 파일입니다.
     */
    private fun notifyAndroidNewImageAdded(imageFile : File) {
        application.sendBroadcast(Intent().apply {
            action = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
            data = Uri.fromFile(imageFile)
        })
    }

}
























