package ru.bikbulatov.comeWithMe.createEvent.ui.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.createEvent.domain.models.ColorGradient
import ru.bikbulatov.comeWithMe.createEvent.ui.ColorPickListener

class ColorsAdapter(
    private val colors: List<ColorGradient>,
    private val colorPickListener: ColorPickListener
) :
    RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {

    init {
        colorPickListener.onColorPicked(colors?.get(0))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_color_circle, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        lateinit var gradientDrawable: GradientDrawable
        if (colors[position].startColor != colors[position].endColor)
            gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                intArrayOf(
                    Color.parseColor(colors[position].startColor),
                    Color.parseColor(colors[position].endColor)
                )
            )
        else {
            gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                intArrayOf(
                    Color.parseColor(colors[position].startColor),
                    Color.parseColor(colors[position].endColor)
                )
            )
        }
        gradientDrawable.cornerRadius = 90f
        holder.clCircle.background = gradientDrawable
        holder.clCircle.setOnClickListener {
            colorPickListener.onColorPicked(colors[position])
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    inner class ColorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clCircle: ConstraintLayout = itemView.findViewById(R.id.clCircle)
    }

}