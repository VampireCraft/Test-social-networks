package ru.bikbulatov.comeWithMe.authorization.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.bikbulatov.comeWithMe.databinding.ActivityAuthBinding

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openLogInFragment()
    }

    private fun openLogInFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(binding.root.id, FragmentLogIn())
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size > 0)
            supportFragmentManager.popBackStack()
        else
            super.onBackPressed()
    }
}