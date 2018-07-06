package com.carlosughini.appmarvel.viewModel

import android.content.Context
import android.content.Intent
import android.databinding.BindingAdapter
import android.widget.ImageView
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.view.activities.DetailCharacterActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class CharacterViewModel(val context: Context, var model: Model.Character) {

    companion object {
        val IMAGE_TYPE = "/landscape_small."
    }

    var imageUrl = modelImageUrl()

    fun modelImageUrl(): String = model.thumbnail.path + IMAGE_TYPE + model.thumbnail.extension

    object ImageViewBindingAdapter {
        @BindingAdapter("bind:imageUrl")
        @JvmStatic
        fun loadImage(view: ImageView, url: String) {
            Picasso.with(view.context).load(url).into(view)
        }
    }

    fun openDetailActivity() {
        val intent = Intent(context, DetailCharacterActivity::class.java)
        val json = Gson().toJson(model)
        intent.putExtra(DetailCharacterActivity.MODEL_EXTRA, json)
        context.startActivity(intent)
    }
}