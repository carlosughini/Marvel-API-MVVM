package com.carlosughini.appmarvel.view.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.carlosughini.appmarvel.R
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.databinding.ActivityDetailBinding
import com.carlosughini.appmarvel.viewModel.CharacterDetailViewModel


class DetailCharacterActivity : AppCompatActivity(), CharacterDetailViewModel.DetailViewModel {

    companion object { val MODEL_EXTRA = "model" }

    lateinit var detailViewModel: CharacterDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        detailViewModel = CharacterDetailViewModel(this, intent)
        detailViewModel.loadCharacter(this)

        binding.viewmodel = detailViewModel
        detailViewModel.loadCollectionViews(binding.dynamicItems, supportFragmentManager)

    }

    override fun endCallProgress(any: Any?) {
        if (any is Throwable) {
            println("Error: " + any.message)
        } else if (any is Model.CharacterResponse) {
            println("response "+ any.data.results[0].name)
        }
    }

    override fun onDestroy() {
        detailViewModel.unsubscribe()
        super.onDestroy()
    }

}