package com.example.match_mate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.match_mate.R
import com.example.match_mate.data.model.User
import com.example.match_mate.utils.MatchScoreUtil


class HomeAdapter(
    private val users: MutableList<User> = mutableListOf(),
    private val loggedInUser: User,
    private val onAcceptClick: (User) -> Unit,
    private val onDeclineClick: (User) -> Unit
) : RecyclerView.Adapter<HomeAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size

    fun updateData(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    fun removeUser(user: User) {
        val position = users.indexOf(user)
        if (position != -1) {
            users.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addUsers(newUsers: List<User>) {
        users.addAll(newUsers)
        notifyItemInserted(users.size - 1)
    }

    fun getUsers(): List<User> {
        return users
    }

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.imageViewProfile)
        private val nameText: TextView = itemView.findViewById(R.id.textViewName)
        private val locationMatchText: TextView = itemView.findViewById(R.id.textViewLocationMatch)
        private val professionText: TextView = itemView.findViewById(R.id.textViewProfession)
        private val buttonAccept: Button = itemView.findViewById(R.id.buttonAccept)
        private val buttonDecline: Button = itemView.findViewById(R.id.buttonDecline)

        fun bind(user: User) {
            val context = itemView.context
            val fullName = "${user.nameFirst}, ${user.dobAge}"
            val locationMatch = "${user.locationCity}, ${user.locationCountry} • ${MatchScoreUtil.calculateMatchScore(loggedInUser, user)}% Match"

            nameText.text = fullName
            locationMatchText.text = locationMatch
            professionText.text = user.education

            if (user.status == "declined" || user.status == "accepted") {
                buttonAccept.visibility = View.GONE
                buttonDecline.visibility = View.GONE
            } else {
                buttonAccept.visibility = View.VISIBLE
                buttonDecline.visibility = View.VISIBLE
            }
            Glide.with(context)
                .load(user.pictureLarge)
                .into(profileImage)

            buttonAccept.setOnClickListener {
                onAcceptClick(user)
            }

            buttonDecline.setOnClickListener {
                onDeclineClick(user)
            }
        }

    }
}
