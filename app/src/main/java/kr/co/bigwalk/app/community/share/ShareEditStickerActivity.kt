package kr.co.bigwalk.app.community.share

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import ja.burhanrashid52.photoeditor.PhotoEditor
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityShareEditStickerBinding
import kr.co.bigwalk.app.util.*
import java.util.*

class ShareEditStickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShareEditStickerBinding
    private val shareEditStickerViewModel by lazy {
        ViewModelProvider(this).get(ShareEditStickerViewModel::class.java)
    }
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private val stickerAdapter: StickerAdapter = StickerAdapter {
        Glide.with(this).asBitmap().load(it.imagePath)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    mPhotoEditor.addImage(resource)
                }
                
                override fun onLoadCleared(placeholder: Drawable?) {
                }
                
            })
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }
    private lateinit var mPhotoEditor: PhotoEditor
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share_edit_sticker)
        binding.lifecycleOwner = this
        
        setView()
        setToolbar()
        bindViewModel()
    }
    
    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_arrow)
            title = ""
        }
    }
    
    private fun setView() {
        with(binding) {
            val bytes = intent.getByteArrayExtra(KEY_IMAGE) ?: return
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            photoEditorView.source.setImageBitmap(bitmap)
            
            val colorDrawable = ColorDrawable(ContextCompat.getColor(this@ShareEditStickerActivity, R.color.black_55))
            
            rvSticker.adapter = stickerAdapter
            
            sheetBehavior = BottomSheetBehavior.from(stickerBottomSheet)
            sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    root.isClickable = newState != BottomSheetBehavior.STATE_EXPANDED
                    invalidateOptionsMenu()
                }
                
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    colorDrawable.alpha = (slideOffset * 255f).toInt()
                    bgView.setBackgroundColor(colorDrawable.color)
                }
                
            })
            mPhotoEditor = PhotoEditor.Builder(this@ShareEditStickerActivity, photoEditorView)
                .setPinchTextScalable(true)
                .build()
        }
    }
    
    private fun bindViewModel() {
        val groupId = intent.getLongExtra(KEY_GROUP_ID, DEF_LONG_VALUE)
        with(shareEditStickerViewModel) {
            getGroupShareContents(groupId)
            stickerItem.observe(this@ShareEditStickerActivity, androidx.lifecycle.Observer {
                stickerAdapter.submitList(it)
            })
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.group_share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.getItem(0).isEnabled = sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.complete -> {
                mPhotoEditor.clearHelperBox()
            }
            R.id.export -> {
                shareScreenshot()
                FirebaseAnalytics.getInstance(this).logEvent("group_share_button_export_click", null)
            }
            R.id.save_image -> {
                galleryAddFile()
                FirebaseAnalytics.getInstance(this).logEvent("group_share_button_save_image", null)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun shareScreenshot() {
        val bitmap = takeScreenShot(binding.stickerScreenshotView)
        val savedScreenshot = screenshotBitmapToFile(bitmap)
        val uri: Uri = getUriFromFile(savedScreenshot)!!
        var sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(sharingIntent, null))
    }
    
    
    private fun galleryAddFile() {
        if (isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            saveBitmapToGallery(takeScreenShot(binding.stickerScreenshotView))
            showToast(getString(R.string.complete_save_image))
            setResult(RESULT_OK)
            finish()
        }
    }
    
    companion object {
        const val KEY_SHARE = 1
        private const val KEY_IMAGE = "IMAGE"
        private const val KEY_GROUP_ID = "GROUP_ID"
        fun getIntent(context: Context, bytes: ByteArray, groupId: Long) =
            Intent(context, ShareEditStickerActivity::class.java)
                .putExtra(KEY_IMAGE, bytes)
                .putExtra(KEY_GROUP_ID, groupId)
    }
}