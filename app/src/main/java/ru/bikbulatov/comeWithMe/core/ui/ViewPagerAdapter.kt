package ru.bikbulatov.comeWithMe.core.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val fragmentList: MutableList<Fragment> = ArrayList()
    var currentFragment: Fragment? = null


    fun addFrag(fragment: Fragment) {
        fragmentList.add(fragment)
    }

    fun replaceFrag(position: Int, fragment: Fragment) {
        fragmentList.removeAt(position)
        fragmentList.add(position, fragment)
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}

