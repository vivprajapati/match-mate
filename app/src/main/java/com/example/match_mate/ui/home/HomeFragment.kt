package com.example.match_mate.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.match_mate.data.api.ApiResult
import com.example.match_mate.databinding.FragmentHomeBinding
import com.example.match_mate.data.model.User
import com.example.match_mate.utils.AppSession
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var userAdapter: HomeAdapter
    private var users: MutableList<User> = mutableListOf()
    var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.allUsersState.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ApiResult.Success -> {
                    isLoading = false
                    if (userAdapter.getUsers().isEmpty()) {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.matchRecyclerView.visibility = View.VISIBLE
                        userAdapter.updateData(result.data)
                        if (userAdapter.getUsers().isEmpty()){
                            binding.emptyLayout.visibility = View.VISIBLE
                            binding.matchRecyclerView.visibility = View.GONE
                        } else {
                            binding.emptyLayout.visibility = View.GONE
                        }
                    } else {
                        userAdapter.addUsers(result.data)
                    }
                }

                is ApiResult.Error -> {
                    isLoading = false
                    val errorMessage = result.message
                    if (userAdapter.getUsers().isEmpty()) {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.errorLayout.visibility = View.VISIBLE
                        binding.matchRecyclerView.visibility = View.GONE
                        binding.errorMessageTextView.text = errorMessage
                        binding.emptyLayout.visibility = View.GONE
                    } else {
                        Snackbar.make(binding.root, errorMessage.toString(), Snackbar.LENGTH_SHORT)
                            .setAction("Retry") {
                                homeViewModel.loadUsers()
                            }
                            .show()
                    }
                }

                ApiResult.Loading -> {
                    if (userAdapter.getUsers().isEmpty()) {
                        binding.loadingProgressBar.visibility = View.VISIBLE
                        binding.errorLayout.visibility = View.GONE
                        binding.matchRecyclerView.visibility = View.GONE
                        binding.emptyLayout.visibility = View.GONE
                    }
                }
            }
        })

        homeViewModel.acceptedUsersState.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ApiResult.Success -> {
                    removeUser(result.data)
                    Snackbar.make(binding.root, "User accepted successfully", Snackbar.LENGTH_SHORT).show()
                }

                is ApiResult.Error -> {
                    Snackbar.make(binding.root, "Failed to accept user: ${result.message}", Snackbar.LENGTH_SHORT).show()
                }

                ApiResult.Loading -> {

                }
            }
        })

        homeViewModel.declinedUsersState.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ApiResult.Success -> {
                    removeUser(result.data)
                    Snackbar.make(binding.root, "User declined successfully", Snackbar.LENGTH_SHORT).show()
                }

                is ApiResult.Error -> {
                    Snackbar.make(binding.root, "Failed to decline user: ${result.message}", Snackbar.LENGTH_SHORT).show()
                }

                ApiResult.Loading -> {

                }
            }
        })

        binding.retryButton.setOnClickListener {
            homeViewModel.loadUsers()
        }
        setupRecyclerView()


        homeViewModel.loadUsers()
    }

    fun removeUser(user: User) {
        userAdapter.removeUser(user)
        loadIfListEmpty()
    }

    fun loadIfListEmpty() {
        if (userAdapter.getUsers().isEmpty()) {
            homeViewModel.loadUsers()
        }
    }

    private fun setupRecyclerView() {
        userAdapter = HomeAdapter(
            users = users,
            loggedInUser = AppSession.currentUser!!,
            onAcceptClick = { user: User ->
                homeViewModel.acceptUser(user)
            },
            onDeclineClick = { user: User ->
                homeViewModel.declineUser(user)
            }
        )
        val layoutManager = LinearLayoutManager(requireContext())
        binding.matchRecyclerView.layoutManager = layoutManager
        binding.matchRecyclerView.adapter = userAdapter

        binding.matchRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val visibleThreshold = 2

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    isLoading = true
                    homeViewModel.loadUsers()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}