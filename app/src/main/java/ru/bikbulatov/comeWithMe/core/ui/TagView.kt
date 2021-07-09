package ru.bikbulatov.comeWithMe.core.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.createEvent.ui.TagClickListener
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel


class TagView(
    context: Context, tag: TagModel, cancelTag: TagClickListener = object : TagClickListener {
        override fun onTagClosed(tagId: Int) {}
    }
) : LinearLayout(context, null) {
    var ivCancel: ImageView
    var tvTitle: TextView
    var clRoot: ConstraintLayout

    init {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_tag, this, true)
        tvTitle = view.findViewById(R.id.tvTitle)
        ivCancel = view.findViewById(R.id.ivCancel)
        clRoot = view.findViewById(R.id.clRoot)
        tvTitle.text = "#${tag.name}"

        ivCancel.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.secondary
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
        ivCancel.setOnClickListener {
            Log.d("test", "ivCancel")
            cancelTag.onTagClosed(tag.id)
        }
    }
}