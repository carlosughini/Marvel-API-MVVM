package com.carlosughini.appmarvel.view.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.view.fragments.CollectionItemFragment

class ComicsCollectionPagerAdapter(fm: FragmentManager, var items: MutableList<Model.Item>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return CollectionItemFragment.newInstance(items[position])
    }

    override fun getCount(): Int {
        return items.size
    }
}