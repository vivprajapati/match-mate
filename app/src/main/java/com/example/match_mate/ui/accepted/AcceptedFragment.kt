package com.example.match_mate.ui.accepted

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.match_mate.R
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.match_mate.data.api.ApiResult
import com.example.match_mate.data.model.User
import com.example.match_mate.databinding.FragmentHomeBinding
import com.example.match_mate.ui.home.HomeAdapter
import com.example.match_mate.ui.home.HomeViewModel
import com.example.match_mate.utils.AppSession
import kotlinx.coroutines.launch


class AcceptedFragment : Fragment() {
    private lateinit var adapter: HomeAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AcceptedViewModel
    private val acceptedList = mutableListOf<User>()

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
        binding.title.text = "Accepted"
        viewModel = ViewModelProvider(this)[AcceptedViewModel::class.java]
        observeAcceptedUsers()
        setUpRecyclerView()
        viewModel.fetchAcceptedUsers()
    }

    fun setUpRecyclerView() {
        adapter = HomeAdapter(
            users = acceptedList,
            loggedInUser = AppSession.currentUser!!,
            onAcceptClick = {},
            onDeclineClick = {}
        )
        val layoutManager = LinearLayoutManager(requireContext())
        binding.matchRecyclerView.layoutManager = layoutManager
        binding.matchRecyclerView.adapter = adapter
    }

    private fun observeAcceptedUsers() {
        viewModel.allUsersState.observe(viewLifecycleOwner) { apiResult ->
            when (apiResult) {
                is ApiResult.Success -> {
                    if (apiResult.data.isEmpty()) {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.matchRecyclerView.visibility = View.GONE
                        binding.emptyLayout.visibility = View.VISIBLE
                    } else {
                        adapter.updateData(apiResult.data)
                        binding.matchRecyclerView.visibility = View.VISIBLE
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.errorLayout.visibility = View.GONE
                        binding.emptyLayout.visibility = View.GONE
                    }

                }

                is ApiResult.Error -> {

                }

                ApiResult.Loading -> {

                }
            }
        }
    }
}