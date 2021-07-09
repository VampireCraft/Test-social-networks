package ru.bikbulatov.comeWithMe.events.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.ui.TagView
import ru.bikbulatov.comeWithMe.events.data.Utils
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel
import ru.bikbulatov.comeWithMe.events.ui.EventsViewModel
import ru.bikbulatov.comeWithMe.events.ui.FragmentEventInside
import java.text.SimpleDateFormat
import java.util.*


class NearbyEventsAdapter(
    val events: List<EventModel>,
    val viewModel: EventsViewModel,
    private val fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<NearbyEventsAdapter.NearbyEventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyEventViewHolder {
        return NearbyEventViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_nearby_event, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NearbyEventViewHolder, position: Int) {
        holder.tvTitle.text = events[position].name
        holder.tvDescription.text = events[position].description
        holder.tvPrice.text = events[position].price.toString()
        holder.tvAddress.text = events[position].address

        if (events[position].isOnline){
            holder.tvIsOnline.visibility = View.VISIBLE
        } else {
            holder.tvIsOnline.visibility = View.GONE
        }

        setDateAndTime(events[position].dateEvent?.toLong(), holder)

        holder.tvOrganizerName.text =
            events[position].eventCreator?.name + ", " + Utils.calcOrganizerAge(
                events[position].eventCreator?.birthday?.toLong()
            )
        loadPhoto(events[position].eventCreator?.photo, holder.ivOrganizerPhoto)

        if (!events[position].photoEvent.isNullOrEmpty())
            loadImageToViewBackground(events[position].photoEvent, holder.clMainInfo)
        else {
            drawBackgroundWithGradient(
                holder.clMainInfo,
                events[position].color?.startColor,
                events[position].color?.endColor
            )
        }
        setTextColor(events[position].color?.textColor, holder)

        holder.clRoot.setOnClickListener {
            fragmentManager
                .beginTransaction()
                .add(R.id.clRoot, FragmentEventInside.createInstance(events[position]))
                .addToBackStack(FragmentEventInside::javaClass.name)
                .commit()
        }

        if (!events[position].tags.isNullOrEmpty())
            addTagsToScreen(events[position].tags, holder)
    }

    private fun setDateAndTime(eventDateTime: Long?, holder: NearbyEventViewHolder) {
        val date = Date()
        date.time = eventDateTime?.times(1000) ?: 0
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        dateFormat.format(date)
        holder.tvTime.text = timeFormat.format(date)
        holder.tvDate.text = dateFormat.format(date)
    }

    private fun loadPhoto(url: String?, imageView: ImageView) {
        Glide
            .with(imageView.context)
            .load(url)
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return true
                }

            })
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.ic_default_profile_photo)
            .into(imageView)
    }

    private fun drawBackgroundWithGradient(view: View, startColor: String?, endColor: String?) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                Color.parseColor(startColor ?: "#000000"),
                Color.parseColor(endColor ?: "#000000")
            )
        )
        gradientDrawable.cornerRadii =
            floatArrayOf(24f, 24f, 24f, 24f, 0f, 0f, 0f, 0f)
        view.background = gradientDrawable
    }

    private fun loadImageToViewBackground(imageUrl: String, view: View) {
        Glide
            .with(view.context)
            .load(imageUrl)
            .centerCrop()
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    resource.alpha = 150
                    view.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun setTextColor(textColor: String?, holder: NearbyEventViewHolder) {
        if (textColor == null)
            return
        holder.tvTitle.setTextColor(Color.parseColor(textColor))
        holder.tvDescription.setTextColor(Color.parseColor(textColor))
        holder.tvPrice.setTextColor(Color.parseColor(textColor))
        holder.tvTime.setTextColor(Color.parseColor(textColor))
        holder.tvDate.setTextColor(Color.parseColor(textColor))
        holder.tvAddress.setTextColor(Color.parseColor(textColor))
    }

    private fun addTagsToScreen(tags: List<TagModel>, holder: NearbyEventViewHolder) {
        val referencesIds = IntArray(tags.size)
        holder.clTags.removeAllViews()
        holder.clTags.addView(holder.tags)
        for ((i, tag) in tags.withIndex()) {
            val tagView =
                TagView(holder.itemView.context, tag)
            tagView.id = View.generateViewId()
            tagView.ivCancel.visibility = View.GONE
            holder.clTags.addView(tagView)
            holder.tags.addView(tagView)
            referencesIds[i] = tagView.id
            holder.tags.referencedIds = referencesIds
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    fun getCurrentEvent(): EventModel {
        return events[1]
    }

    inner class NearbyEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val tvIsOnline: TextView = itemView.findViewById(R.id.tvIsOnline)
        val tvOrganizerName: TextView = itemView.findViewById(R.id.tvOrganizerName)
        val ivOrganizerPhoto: ImageView = itemView.findViewById(R.id.ivOrganizerPhoto)
        val clMainInfo: ConstraintLayout = itemView.findViewById(R.id.clMainInfo)
        val clRoot: ConstraintLayout = itemView.findViewById(R.id.clRoot)
        val clTags: ConstraintLayout = itemView.findViewById(R.id.clTags)
        val tags: Flow = itemView.findViewById(R.id.tags)
    }
}