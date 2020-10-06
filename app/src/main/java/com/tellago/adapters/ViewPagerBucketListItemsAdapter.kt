package com.tellago.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

public class ViewPagerBucketListItemsFragmentStateAdapter(fa: FragmentActivity, private val fragments: ArrayList<Fragment>): FragmentStateAdapter(fa)

{

    fun getItem(position: Int): Fragment {
        return fragments[position]
    }


    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}