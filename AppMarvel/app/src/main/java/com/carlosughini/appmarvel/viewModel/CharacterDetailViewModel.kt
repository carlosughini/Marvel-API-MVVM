package com.carlosughini.appmarvel.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.databinding.BindingAdapter
import android.support.v4.app.FragmentManager
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import com.carlosughini.appmarvel.BuildConfig
import com.carlosughini.appmarvel.R
import com.carlosughini.appmarvel.api.MarvelApi
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.utils.UtilsHashMd5
import com.carlosughini.appmarvel.view.activities.DetailCharacterActivity
import com.carlosughini.appmarvel.view.collectionView.ComicsCollectionView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*

class CharacterDetailViewModel(val context: Context, var intent: Intent) {

    var service: MarvelApi = MarvelApi.create()
    var model: Model.Character
    companion object { val IMAGE_TYPE = "/standard_large." }

    init {
        val characterType = object : TypeToken<Model.Character>() {}.type
        model = Gson().fromJson<Model.Character>(intent.getStringExtra(DetailCharacterActivity.MODEL_EXTRA), characterType)
    }

    interface DetailViewModel { fun endCallProgress(any: Any?) }

    var detailImageUrl = detailImageUrl()

    private fun detailImageUrl(): String = model.thumbnail.path + IMAGE_TYPE + model.thumbnail.extension

    object ImageViewBindingAdapter {
        @BindingAdapter("bind:detailImageUrl")
        @JvmStatic
        fun loadImage(view: ImageView, url: String) {
            Picasso.with(view.context).load(url).into(view)
        }
    }

    private var _compoSub = CompositeSubscription()
    private val compoSub: CompositeSubscription
        get() {
            if (_compoSub.isUnsubscribed) {
                _compoSub = CompositeSubscription()
            }
            return _compoSub
        }

    private fun manageSub(s: Subscription) = compoSub.add(s)

    fun unsubscribe() { compoSub.unsubscribe() }

    @SuppressLint("LogNotTimber")
    fun loadCharacter(callback: DetailViewModel) {
        val timestamp = Date().time;
        val hash = UtilsHashMd5.md5(timestamp.toString()+ BuildConfig.MARVEL_PRIVATE_KEY+ BuildConfig.MARVEL_PUBLIC_KEY)

        manageSub(
                service.getCharacterInfo(model.id.toString(), timestamp.toString(), BuildConfig.MARVEL_PUBLIC_KEY, hash)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( { c -> callback.endCallProgress(c)}
                        ) { e -> callback.endCallProgress(e)
                            Log.e(DetailCharacterActivity::class.java.simpleName, e.message)}
        )

    }


    fun loadCollectionViews(view: LinearLayout, fm: FragmentManager) {
        if (model.comics.items.size != 0) {
            view.addView(ComicsCollectionView(context, model.comics, fm, context.getString(R.string.collection_item_comics_title)))
        }
    }


}