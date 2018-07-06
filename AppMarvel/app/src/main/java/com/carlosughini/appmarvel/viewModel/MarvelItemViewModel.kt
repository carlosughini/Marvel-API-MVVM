package com.carlosughini.appmarvel.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.carlosughini.appmarvel.BuildConfig
import com.carlosughini.appmarvel.api.MarvelApi
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.utils.UtilsHashMd5
import com.carlosughini.appmarvel.view.activities.DetailCharacterActivity
import com.carlosughini.appmarvel.view.fragments.CollectionItemFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*

class MarvelItemViewModel(val context: Context, var model: Model.Item) {

    var service: MarvelApi = MarvelApi.create()
    lateinit var detailModel: Model.Detail

    companion object {
        fun putBundleArgs(model: Model.Item): Bundle {
            val args = Bundle()
            args.putString(DetailCharacterActivity.MODEL_EXTRA, Gson().toJson(model))
            return args
        }

        fun getModelFromBundle(args: Bundle): Model.Item {
            val itemType = object : TypeToken<Model.Item>() {}.type
            return Gson().fromJson<Model.Item>(args.getString(DetailCharacterActivity.MODEL_EXTRA), itemType)
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

    fun imageUrl(): String = detailModel.images[0].path+"/portrait_medium."+detailModel.images[0].extension

    interface DetailFragmentViewModel { fun endCallProgress(any: Any?) }

    @SuppressLint("LogNotTimber")
    fun loadDetail(callback: DetailFragmentViewModel, resourceURI: String) {

        val url = resourceURI.replace(MarvelApi.BASE_URL, "")
        val splittedURI = url.split("/")

        val timestamp = Date().time
        val hash = UtilsHashMd5.md5(timestamp.toString()+ BuildConfig.MARVEL_PRIVATE_KEY+ BuildConfig.MARVEL_PUBLIC_KEY)

        manageSub(
                service.getDetail(splittedURI[0], splittedURI[1], timestamp.toString(), BuildConfig.MARVEL_PUBLIC_KEY, hash)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( { c -> callback.endCallProgress(c)}
                        ) { e -> callback.endCallProgress(e)
                            Log.e(CollectionItemFragment::class.java.simpleName, e.message)}
        )

    }




}