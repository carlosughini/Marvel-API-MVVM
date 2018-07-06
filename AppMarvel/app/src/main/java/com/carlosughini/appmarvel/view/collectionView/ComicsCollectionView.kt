package com.carlosughini.appmarvel.view.collectionView

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.carlosughini.appmarvel.R
import com.carlosughini.appmarvel.databinding.ViewCollectionBinding
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.view.adapters.ComicsCollectionPagerAdapter
import com.carlosughini.appmarvel.viewModel.CollectionItemViewModel

class ComicsCollectionView : RelativeLayout {

    lateinit var viewModel: CollectionItemViewModel
    lateinit var fm: FragmentManager

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, model: Model.CollectionItem, fragmentManager: FragmentManager, title: String) : super(context) {
        viewModel = CollectionItemViewModel(context, model, title)
        fm = fragmentManager
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        val binding = DataBindingUtil.inflate<ViewCollectionBinding>(LayoutInflater.from(context), R.layout.view_collection, this, true)
        binding.viewmodel = viewModel
        binding.collectionPager.adapter = ComicsCollectionPagerAdapter(fm, viewModel.model.items)

    }

}