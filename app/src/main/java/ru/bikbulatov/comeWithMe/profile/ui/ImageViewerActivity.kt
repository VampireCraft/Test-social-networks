package ru.bikbulatov.comeWithMe.profile.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

private const val IMAGE = "IMAGE"

class ImageViewerActivity : AppCompatActivity() {

    private var activityContentView: View? = null
    private lateinit var context: Context

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        context = this
        val url = intent.extras?.getString(IMAGE)
        val imageView = ImageView(this)

        if (url != null) {
            Glide.with(imageView)
                .load(url)
                .into(imageView)
            setContentView(imageView)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activityContentView = findViewById(android.R.id.content)
            activityContentView!!.setBackgroundColor(-0x1000000)
        }

        imageView.setOnTouchListener(object : OnSwipeTouchListener(this) {

            override fun onSwipeBottom() {
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        })
    }
}
