package ru.bikbulatov.comeWithMe.plans.ui.whereIGo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.databinding.FragmentEventsWhereIGoBinding
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.plans.ui.PlansViewModel
import ru.bikbulatov.comeWithMe.plans.ui.adapter.PlansEventsAdapter


class FragmentEventsWhereIGo : Fragment() {
    private lateinit var binding: FragmentEventsWhereIGoBinding
    private lateinit var viewModel: PlansViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsWhereIGoBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(PlansViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAcceptedEvents()
        observeOnAcceptedEvents()
        refreshOnSwipe()
    }

    private fun refreshOnSwipe() {
        binding.swipeRefreshLayout.setDistanceToTriggerSync(300)
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAcceptedEvents()
        }
    }

    private fun observeOnAcceptedEvents() {
        viewModel.acceptedEvents.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    binding.pbLoading.visibility = View.VISIBLE
                    Log.d("acceptedEvents", "LOADING")
                }
                Status.SUCCESS -> {
                    binding.pbLoading.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    Log.d("acceptedEvents", "SUCCESS")
                    it.data?.let { events ->
                        configureMyEvents(events)
                    }
                }
                Status.ERROR -> {
                    binding.pbLoading.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    Log.d("acceptedEvents", "ERROR")
                }
            }
        })
    }

    private fun configureMyEvents(events: List<EventModel>) {
        if (!events.isNullOrEmpty()) {
            binding.rvEvents.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvEvents.adapter =
                PlansEventsAdapter(parentFragmentManager, events, isMyEvent = true)
        } else {
            binding.tvEmptyEvents.visibility = View.VISIBLE
            binding.rvEvents.visibility = View.GONE
        }
    }
}