package ru.bikbulatov.comeWithMe.plans.ui.adapter

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.databinding.ItemPlansEventBinding
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel
import ru.bikbulatov.comeWithMe.plans.ui.myEvent.FragmentMyEventInside
import ru.bikbulatov.comeWithMe.plans.ui.whereIGo.FragmentEventWhereIGoInside
import java.text.SimpleDateFormat
import java.util.*


class PlansEventsAdapter(
    private val fragmentManager: FragmentManager,
    val events: List<EventModel>,
    val isMyEvent: Boolean
) :
    RecyclerView.Adapter<PlansEventsAdapter.PlansEventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlansEventViewHolder {
        return PlansEventViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_plans_event, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlansEventViewHolder, position: Int) {
        holder.tvTitle.text = events[position].name
        holder.tvOrganizerName.text =
            events[position].eventCreator?.name + ", " + calcOrganizerAge(events[position].eventCreator?.birthday?.toLong())
        setDate(events[position].dateEvent?.toLong(), holder)

        if (!events[position].photoEvent.isNullOrEmpty()) {
            loadImageToViewBackground(events[position].photoEvent, holder.clEventInfo)
        } else if (events[position].color != null) {
            drawBackgroundWithGradient(
                holder.clEventInfo,
                events[position].color?.startColor,
                events[position].color?.endColor
            )
            events[position].color?.textColor?.let { setTextColor(it, holder) }
        }

        if (events[position].isOnline){
            holder.tvIsOnline.visibility = View.VISIBLE
        } else {
            holder.tvIsOnline.visibility = View.GONE
        }

        loadUserPhoto(holder, events[position].eventCreator?.photo)
            //        addTagsToScreen(events[position].tags.take(3), holder)
        setOnEventClick(holder.clRoot, isMyEvent, events[position])
    }

    private fun loadUserPhoto(holder: PlansEventViewHolder, url: String?) {
        Glide
            .with(holder.itemView.context)
            .load(url)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.ic_default_profile_photo)
            .into(holder.ivOrganizerPhoto)
    }

    private fun drawBackgroundWithGradient(view: View, startColor: String?, endColor: String?) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BL_TR,
            intArrayOf(
                if (startColor != null) Color.parseColor(startColor) else ContextCompat.getColor(
                    view.context,
                    R.color.main
                ),
                if (endColor != null) Color.parseColor(endColor) else ContextCompat.getColor(
                    view.context,
                    R.color.main
                )
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
            .apply(RequestOptions.bitmapTransform(GranularRoundedCorners(24f, 24f, 0f, 0f)))
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

    private fun setDate(
        eventDateTime: Long?,
        holder: PlansEventViewHolder
    ) {
        val date = Date()
        date.time = eventDateTime?.times(1000) ?: 0
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        holder.tvDate.text = dateFormat.format(date)
    }

    private fun calcOrganizerAge(birthDay: Long?): String {
        if (birthDay == null)
            return " "
        val calendar = Calendar.getInstance().apply {
            timeInMillis = birthDay * 1000
        }
        return (Calendar.getInstance().get(Calendar.YEAR) - calendar.get(Calendar.YEAR)).toString()
    }

    private fun addTagsToScreen(tags: List<TagModel>, holder: PlansEventViewHolder) {
        when (tags.size) {
            1 -> {
                holder.binding.firstTag.root.visibility = View.VISIBLE
            }
            2 -> {
                holder.binding.firstTag.root.visibility = View.VISIBLE
                holder.binding.secondTag.root.visibility = View.VISIBLE
            }
            3 -> {
                holder.binding.firstTag.root.visibility = View.VISIBLE
                holder.binding.secondTag.root.visibility = View.VISIBLE
                holder.binding.thirdTag.root.visibility = View.VISIBLE
            }
        }

        for ((i, tag) in tags.withIndex()) {
            when (i) {
                0 -> {
                    holder.binding.firstTag.tvTitle.text = "#" + tag.name
                }
                1 -> {
                    holder.binding.secondTag.tvTitle.text = "#" + tag.name
                }
                2 -> {
                    holder.binding.thirdTag.tvTitle.text = "#" + tag.name
                }
            }
        }
    }

    private fun setTextColor(textColor: String, holder: PlansEventViewHolder) {
        holder.tvTitle.setTextColor(Color.parseColor(textColor))
    }

    private fun setOnEventClick(
        holdersRoot: ConstraintLayout,
        isMyEvent: Boolean,
        event: EventModel
    ) {
        holdersRoot.setOnClickListener {
            openEventInside(isMyEvent, event)
        }
    }

    private fun openEventInside(isMyEvent: Boolean, event: EventModel) {
        val fragment: Fragment = if (isMyEvent)
            FragmentMyEventInside.createInstance(event)
        else
            FragmentEventWhereIGoInside.createInstance(event)

        fragmentManager
            .beginTransaction()
            .replace(R.id.flEventsRoot, fragment)
            .addToBackStack(fragment::class.simpleName)
            .commit()
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class PlansEventViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = ItemPlansEventBinding.bind(itemView)

        val tvTitle: TextView = binding.tvTitle
        val tvOrganizerName: TextView = binding.tvOrganizerName
        val tvDate: TextView = binding.tvDate
        val tvIsOnline: TextView = binding.tvIsOnline
        val ivOrganizerPhoto: ImageView = binding.ivOrganizerPhoto
        val ivCancel: ImageView = binding.ivCancelEvent
        val clEventInfo: ConstraintLayout = binding.clEventInfo
        val clRoot: ConstraintLayout = binding.clRoot
        val tags: Flow = binding.tags
    }
}