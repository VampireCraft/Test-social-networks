package ru.bikbulatov.comeWithMe.plans.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.events.ui.EventsViewModel
import ru.bikbulatov.comeWithMe.plans.ui.FragmentEventMembers
import ru.bikbulatov.comeWithMe.plans.ui.FragmentPlans
import ru.bikbulatov.comeWithMe.profile.domain.models.UserModel
import ru.bikbulatov.comeWithMe.profile.ui.FragmentAboutUser
import java.util.*

class MembersAdapter(
    private val users: List<UserModel>,
    private val fragmentManager: FragmentManager,
    private val eventType: Int,
    private val membersListType: Int,
    private val eventId: Int,
    private val viewModel: EventsViewModel
) :
    RecyclerView.Adapter<MembersAdapter.MemberHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        return MemberHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_with_rating, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.tvUserName.text =
            users[position].name + ", ${calcOrganizerAge(users[position].birthday?.toLong())}"
        holder.tvRating.text = users[position].totalRating.toString()
        holder.clUserRoot.setOnClickListener {
            fragmentManager
                .beginTransaction()
                .replace(
                    R.id.clMembersRoot,
                    FragmentAboutUser.createInstance(userId = users[position].id)
                )
                .addToBackStack(FragmentAboutUser::class.simpleName)
                .commit()
        }

        if (eventType == FragmentPlans.EVENT_CREATED_BY_ME && membersListType == FragmentEventMembers.MODERATING_USER_LIST) {
            holder.ivMenu.visibility = View.VISIBLE
            holder.ivMenu.setOnClickListener {
                val popupMenu = PopupMenu(holder.itemView.context, holder.ivMenu)
                popupMenu.menuInflater.inflate(R.menu.menu_members, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_add ->
                            viewModel.addUserOnEvent(
                                userId = users[position].id,
                                eventId = eventId
                            )

                        R.id.menu_delete -> {
                            viewModel.deleteUserFromEvent(
                                userId = users[position].id,
                                eventId = eventId
                            )
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }

        loadUserPhoto(holder.ivUserPhoto, users[position].photo)
    }

    private fun loadUserPhoto(imageView: ImageView, url: String?) {
        Glide
            .with(imageView.context)
            .load(url)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .placeholder(R.drawable.ic_default_profile_photo)
            .into(imageView)
    }

    private fun calcOrganizerAge(birthDay: Long?): String {
        if (birthDay == null)
            return " "
        val calendar = Calendar.getInstance().apply {
            timeInMillis = birthDay * 1000
        }
        return (Calendar.getInstance().get(Calendar.YEAR) - calendar.get(Calendar.YEAR)).toString()
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class MemberHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivUserPhoto: ImageView = itemView.findViewById(R.id.ivUserPhoto)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val ivMenu: ImageView = itemView.findViewById(R.id.ivMenu)
        val clUserRoot: ConstraintLayout = itemView.findViewById(R.id.clUserRoot)
    }
}