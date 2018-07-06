package com.carlosughini.appmarvel.view.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carlosughini.appmarvel.databinding.ItemCollectionBinding
import com.carlosughini.appmarvel.R
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.view.activities.DetailCharacterActivity
import com.carlosughini.appmarvel.viewModel.MarvelItemViewModel
import com.squareup.picasso.Picasso

class CollectionItemFragment : Fragment(), MarvelItemViewModel.DetailFragmentViewModel {

    lateinit var viewModel: MarvelItemViewModel
    lateinit var binding: ItemCollectionBinding
    var loaded = false

    override fun endCallProgress(any: Any?) {
        if (any is Model.DetailResponse) {
            println("fragment response working: " + any.toString())
            viewModel.detailModel = any.data.results[0]
            binding.itemName.text = viewModel.detailModel.title
            Picasso.with(context).load(viewModel.imageUrl()).into(binding.itemImage)
        }
    }

    companion object {
        fun newInstance(model: Model.Item): CollectionItemFragment {
            val fragment = CollectionItemFragment.newInstance()
            fragment.arguments = MarvelItemViewModel.putBundleArgs(model)

            return fragment
        }

        fun newInstance(): CollectionItemFragment {
            return CollectionItemFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ItemCollectionBinding>(inflater, R.layout.item_collection, container, false)

        if(!loaded && arguments != null && arguments!!.containsKey(DetailCharacterActivity.MODEL_EXTRA)) {
            viewModel = MarvelItemViewModel(this.context!!, MarvelItemViewModel.getModelFromBundle(arguments!!))
            viewModel.loadDetail(this, viewModel.model.resourceURI)
        }

        return binding.root
    }

}