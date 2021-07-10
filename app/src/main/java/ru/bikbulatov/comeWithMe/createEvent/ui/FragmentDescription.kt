package ru.bikbulatov.comeWithMe.createEvent.ui

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ru.bikbulatov.comeWithMe.R
import ru.bikbulatov.comeWithMe.core.model.Status
import ru.bikbulatov.comeWithMe.core.ui.BaseFragment
import ru.bikbulatov.comeWithMe.core.ui.TagView
import ru.bikbulatov.comeWithMe.createEvent.ui.vm.CreateEventViewModel
import ru.bikbulatov.comeWithMe.databinding.FragmentCreateDescriptionBinding
import ru.bikbulatov.comeWithMe.events.domain.models.TagModel

class FragmentDescription : BaseFragment() {
    private lateinit var binding: FragmentCreateDescriptionBinding
    private lateinit var viewModel: CreateEventViewModel

    companion object {
        const val MAX_USERS_COUNT = 500
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateDescriptionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(CreateEventViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTags()
        configureBtnNext()
        configureBackBtn()
        observeOnTextFields()
        paintProgressBar()
        observeOnTags()
        observeOnSelectedTags()
    }

    private fun observeOnSelectedTags() {
        viewModel.selectedTags.observe(viewLifecycleOwner, {
            viewModel.eventCreationRequest.tags = it
            addTagsToScreen(it)
        })
    }

    private fun observeOnTags() {
        viewModel.tags.observe(viewLifecycleOwner, {
            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (it.data?.isNotEmpty() == true) {
                        val tagsNames = mutableListOf<String>()
                        for (tag in it.data)
                            tagsNames.add(tag.name)
                        binding.atvTags.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                tagsNames
                            )
                        )
                        binding.atvTags.threshold = 1
                        binding.atvTags.onItemClickListener =
                            AdapterView.OnItemClickListener { parent, view, position, id ->
                                //todo в случае, когда массив опустеет, то это отвалится
                                val bufferList = viewModel.selectedTags.value
                                bufferList?.add(viewModel.tags.value?.data?.find { it.name == (view as? AppCompatTextView)?.text }!!)
                                viewModel.selectedTags.value = bufferList
                                binding.atvTags.setText("")
                            }
                    }
                }

                Status.ERROR -> {
                }
            }
        })
    }

    private fun observeOnTextFields() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    viewModel.eventCreationRequest.name = s.toString()
                } catch (e: Exception) {
                    showError("Неверный формат данных")
                }
            }
        })

        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    viewModel.eventCreationRequest.description = s.toString()
                } catch (e: Exception) {
                    showError("Неверный формат данных")
                }
            }
        })

        binding.etMembersCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s.toString()?.toInt() > MAX_USERS_COUNT) {
                        binding.etMembersCount.setText("$MAX_USERS_COUNT")
                        showError("Максимальное количество участников - $MAX_USERS_COUNT")
                    }
                    viewModel.eventCreationRequest.maxCountUsers = s.toString()?.toInt()
                } catch (e: Exception) {

                    showError("Неверный формат данных")
                }
            }
        })
    }

    private fun paintProgressBar() {
//        binding.pbEventCreate.indeterminateDrawable.colorFilter =
//            PorterDuffColorFilter(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.bright_turquoise
//                ), PorterDuff.Mode.SRC_IN
//            )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.pbEventCreate.setProgress(40, true)
        } else
            binding.pbEventCreate.progress = 40
    }

    private fun configureBtnNext() {
        binding.btnNext.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.btn_click))
            if (isNameValidOrShowError()) {
                if (isDescriptionValidOrShowError()) {
                    if (!isMembersCountValidOrShowError())
                        fillUsersCount()
                    viewModel.navigator?.openSettingsScreen()
                }
            }
        }
    }

    private fun fillUsersCount() {
        viewModel.eventCreationRequest.maxCountUsers = MAX_USERS_COUNT
    }

    private fun configureBackBtn() {
        binding.ivBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun isNameValidOrShowError(): Boolean {
        val name = viewModel.eventCreationRequest.name
        return if (!name?.isNullOrEmpty() && name.length > 3) {
            true
        } else {
            showError("Название мероприятия должно содержать не менее 4-ех символов")
            false
        }
    }

    private fun isDescriptionValidOrShowError(): Boolean {
        val description = viewModel.eventCreationRequest.description
        if (description.isEmpty()) {
            showError("Напишите описание вашему событию!")
            return false
        }
        return true
    }

    private fun isMembersCountValidOrShowError(): Boolean {
        val membersCount = viewModel.eventCreationRequest.maxCountUsers
        if (membersCount <= 0 || membersCount > MAX_USERS_COUNT)
            return false
        return true
    }

    private fun addTagsToScreen(tags: List<TagModel>) {
        val referencesIds = IntArray(tags.size)
        binding.clTags.removeAllViews()
        binding.clTags.addView(binding.tags)
        for ((i, tag) in tags.withIndex()) {
            val tagView =
                TagView(requireContext(), tag, cancelTag = object : TagClickListener {
                    override fun onTagClosed(tagId: Int) {
                        deleteTag(tag.id)
                    }
                })
            tagView.id = tag.id
            tagView.ivCancel.visibility = View.VISIBLE
            tagView.clRoot.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.decoration_rounded_yellow_border
            )
            tagView.tvTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.secondary
                )
            )
            binding.clTags.addView(tagView)
            binding.tags.addView(tagView)
            referencesIds[i] = tagView.id
        }
        binding.tags.referencedIds = referencesIds
    }

    fun deleteTag(tagId: Int) {
//        if (binding.clRoot.findViewById<TagView>(tagId) != null) {
//            binding.clRoot.removeView(binding.clRoot.findViewById(tagId))
//            var tagsIdList = binding.tags.referencedIds
//            tagsIdList = tagsIdList.filter { it != tagId }.toIntArray()
//            binding.tags.referencedIds = tagsIdList
//            viewModel.selectedTags.value =
//                viewModel.selectedTags.value?.filter { it.id != tagId }?.toMutableList()
//            Log.d("test", "deleted tag $tagId")
//        }
//        Log.d("test", "onTagCloseClick $tagId")

        Log.d("test", "deleted tag $tagId")
        viewModel.selectedTags.value =
            viewModel.selectedTags.value?.filter { it.id != tagId }?.toMutableList()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }
}