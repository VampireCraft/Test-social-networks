package ru.bikbulatov.comeWithMe.plans.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.ui.ViewPagerAdapter
import ru.bikbulatov.comeWithMe.databinding.FragmentPlansBinding
import ru.bikbulatov.comeWithMe.plans.ui.myEvent.FragmentMyEvents
import ru.bikbulatov.comeWithMe.plans.ui.whereIGo.FragmentEventsWhereIGo

class FragmentPlans : Fragment() {
    companion object {
        const val EVENT_WHERE_I_GO = 0
        const val EVENT_CREATED_BY_ME = 1
    }

    private lateinit var binding: FragmentPlansBinding
    private lateinit var viewModel: PlansViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlansBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(PlansViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureViewPager()
        configureNavigation()
    }

    private fun configureNavigation() {
        binding.btnWhereIGo.setOnClickListener {
            binding.vpUserEvents.setCurrentItem(EVENT_WHERE_I_GO, true)
            it.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.decoration_rounded_yellow_filled
            )
            binding.btnCreatedByMe.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.decoration_rounded_yellow_border
            )
            binding.btnWhereIGo.setTextColor(ContextCompat.getColor(it.context, R.color.main))
            binding.btnCreatedByMe.setTextColor(
                ContextCompat.getColor(
                    it.context,
                    R.color.secondary
                )
            )
        }

        binding.btnCreatedByMe.setOnClickListener {
            binding.vpUserEvents.setCurrentItem(EVENT_CREATED_BY_ME, true)
            it.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.decoration_rounded_yellow_filled
            )
            binding.btnWhereIGo.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.decoration_rounded_yellow_border
            )
            binding.btnCreatedByMe.setTextColor(ContextCompat.getColor(it.context, R.color.main))
            binding.btnWhereIGo.setTextColor(ContextCompat.getColor(it.context, R.color.secondary))
        }
    }

    private fun configureViewPager() {
        val adapter = ViewPagerAdapter(requireActivity())
        adapter.addFrag(FragmentEventsWhereIGo())
        adapter.addFrag(FragmentMyEvents())
        binding.vpUserEvents.isUserInputEnabled = false
        binding.vpUserEvents.adapter = adapter
        binding.vpUserEvents.adapter?.notifyDataSetChanged()
    }
}