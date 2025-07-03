package com.example.match_mate.ui.declined

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.match_mate.R


import android.view.*
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.match_mate.data.model.User
import com.example.match_mate.ui.home.HomeAdapter
import com.example.match_mate.utils.AppSession
import kotlinx.coroutines.launch

class DeclinedFragment : Fragment() {

    private lateinit var declinedRecyclerView: RecyclerView
    private lateinit var adapter: HomeAdapter

    private val viewModel: DeclinedViewModel by viewModels()
    private var loggedInUser: User? = null
    private val declinedList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false) // Reuse same layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        declinedRecyclerView = view.findViewById(R.id.matchRecyclerView)

        lifecycleScope.launch {
            loggedInUser = AppSession.currentUser
            if (loggedInUser == null) {
                Toast.makeText(requireContext(), "No logged-in user found", Toast.LENGTH_SHORT).show()
                return@launch
            }

            adapter = HomeAdapter(
                users = declinedList,
                loggedInUser = loggedInUser!!,
                onAcceptClick = {}, // No actions needed
                onDeclineClick = {} // No actions needed
            )

            declinedRecyclerView.adapter = adapter

            observeDeclinedUsers()
        }
    }

    private fun observeDeclinedUsers() {
        viewModel.declinedUsers.observe(viewLifecycleOwner) { users ->
            declinedList.clear()
            declinedList.addAll(users)
            adapter.updateData(declinedList)
        }
    }
}