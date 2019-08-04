package com.gmail.ayteneve93.apex.kakaopay_preassignment.controller

import android.app.AlertDialog
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private val mImageModelMap : HashMap<String, KakaoImageModel> = HashMap()
    fun isImageModelExists(imageModel: KakaoImageModel) : Boolean = mImageModelMap.containsKey(imageModel.imageUrl)
    fun addImageModel(imageModel: KakaoImageModel) { mImageModelMap.put(imageModel.imageUrl, imageModel) }
    fun removeImageModel(imageModel: KakaoImageModel) = mImageModelMap.remove(imageModel.imageUrl)
    fun clearImageModels() = mImageModelMap.clear()

    fun startShare(acitivty : AppCompatActivity) {
        Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            val urlList : ArrayList<Uri> = ArrayList()
            mImageModelMap.keys.forEach {
                urlList.add(Uri.parse(it))
            }
            //putParcelableArrayListExtra(Intent.EXTRA_STREAM, urlList)

            type = "text/plain"


            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            acitivty.startActivity(Intent.createChooser(this, "Share images to..."))
        }
    }

    fun startDownload() {
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
                    if(isWifiConnected) runDownloadProcess(clonedImageModelMap)
                    else {
                        AlertDialog.Builder(application).apply {
                            setTitle(R.string.dialog_image_download_wifi_warning_title)
                            setMessage(R.string.dialog_image_download_wifi_warning_message)
                            setPositiveButton(R.string.dialog_image_download_wifi_warning_positive_button_txt) { _, _ ->
                                runDownloadProcess(clonedImageModelMap)
                            }
                            setNegativeButton(R.string.dialog_image_download_wifi_warning_negative_button_txt) { _, _ ->
                                downloadFailed()
                            }
                        }
                    }
                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                    downloadFailed()
                }

                private fun downloadFailed() {
                    Toast.makeText(application, R.string.txt_image_download_failed, Toast.LENGTH_LONG).show()
                }

            })
            .setRationaleMessage(R.string.permission_external_storage_rational_message)
            .setDeniedMessage(R.string.permission_external_storage_denied_message)
            .setGotoSettingButton(true)
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    private fun runDownloadProcess(clonedImageModelMap : HashMap<String, KakaoImageModel>) {
        val totalImageCount = clonedImageModelMap.size
        var currentImageCount = 0

        clonedImageModelMap.forEach {
            Glide.with(application)
                .asBitmap()
                .load(it.value.imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                    override fun onResourceReady(bitmapImage: Bitmap, transition: Transition<in Bitmap>?) {
                        val imageFile = File(mDownloadDirectory, application.getString(R.string.download_image_prefix, it.value.hashCode()) + ".jpg")
                        try {
                            FileOutputStream(imageFile).also { fileOutputStream ->
                                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                                fileOutputStream.close()
                                notifyAndroidNewImageAdded(imageFile)
                                currentImageCount++
                                if(currentImageCount == totalImageCount) Toast.makeText(application, application.getString(R.string.txt_image_download_succeed), Toast.LENGTH_LONG).show()
                            }
                        } catch(e : Exception) { e.printStackTrace() }
                    }
                })
        }
    }

    private fun notifyAndroidNewImageAdded(imageFile : File) {
        application.sendBroadcast(Intent().apply {
            action = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
            data = Uri.fromFile(imageFile)
        })
    }

}
