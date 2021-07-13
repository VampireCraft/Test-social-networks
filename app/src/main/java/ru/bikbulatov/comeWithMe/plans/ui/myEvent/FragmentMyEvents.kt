package ru.bikbulatov.comeWithMe.plans.ui.myEvent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.bikbulatov.comeWithMe.core.model.Event
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.databinding.FragmentMyEventsBinding
import ru.bikbulatov.comeWithMe.events.domain.models.EventModel
import ru.bikbulatov.comeWithMe.plans.ui.PlansViewModel
import ru.bikbulatov.comeWithMe.plans.ui.adapter.PlansEventsAdapter

class FragmentMyEvents : Fragment(), MyEventManager {
    private lateinit var binding: FragmentMyEventsBinding
    private lateinit var viewModel: PlansViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyEventsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(PlansViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshList()
    }

    override fun refreshList() {
        viewModel.getMyEvents()
        observeOnMyEvents()
        refreshOnSwipe()
    }

    private fun refreshOnSwipe() {
        binding.swipeRefreshLayout.setDistanceToTriggerSync(300)
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getMyEvents()
        }
    }

    private fun observeOnMyEvents() {
        viewModel.myEvents.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                    binding.pbLoading.visibility = View.VISIBLE
                    Log.d("myEvents", "LOADING")
                }

                Status.SUCCESS -> {
                    binding.pbLoading.visibility = View.GONE
                    Log.d("myEvents", "SUCCESS")
                    it.data?.let { events ->
                        configureMyEvents(events)
                    }


                    binding.swipeRefreshLayout.isRefreshing = false
                }
                Status.ERROR -> {
                    Log.d("myEvents", "ERROR")
                    binding.pbLoading.visibility = View.VISIBLE
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