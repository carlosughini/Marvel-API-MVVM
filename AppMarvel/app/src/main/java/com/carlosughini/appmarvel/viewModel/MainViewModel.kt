package com.carlosughini.appmarvel.viewModel

import android.content.Context
import android.support.v7.widget.SearchView
import android.util.Log
import com.carlosughini.appmarvel.BuildConfig
import com.carlosughini.appmarvel.api.MarvelApi
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.utils.UtilsHashMd5
import com.carlosughini.appmarvel.view.activities.MainActivity
import com.carlosughini.appmarvel.view.adapters.CharactersAdapter
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*

class MainViewModel(val context: Context) {

    lateinit var originalList: MutableList<Model.Character>
    val defaultLimit = 20
    var countLimit = 0
    private var service: MarvelApi = MarvelApi.create()

    interface MainActivityViewModel { fun endCallProgress(any: Any?) }

    private var _compoSub = CompositeSubscription()
    private val compoSub: CompositeSubscription
        get() {
            if (_compoSub.isUnsubscribed) {
                _compoSub = CompositeSubscription()
            }
            return _compoSub
        }

    private final fun manageSub(s: Subscription) = compoSub.add(s)

    fun unsubscribe() { compoSub.unsubscribe() }

    fun filterList(term: String, adapter: CharactersAdapter) {
        if (term != "") {
            val list = adapter.characterResponse.data.results.filter { it.name.contains(term.trim(), true) } as MutableList<Model.Character>
            adapter.characterResponse.data.results = list
            adapter.notifyDataSetChanged()

        } else {
            adapter.characterResponse.data.results = originalList
            adapter.notifyDataSetChanged()
        }

    }

    fun getOnQueryTextChange(adapter: CharactersAdapter) : SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener{
        override fun onQueryTextChange(term: String?): Boolean {
            if (term != null) { filterList(term, adapter) }
            return false
        }
        override fun onQueryTextSubmit(term: String?): Boolean {
            if (term != null) { filterList(term, adapter) }
            return false
        }
    }


    fun loadCharactersList(callback: MainActivityViewModel) {

        val timestamp = Date().time;
        val hash = UtilsHashMd5.md5(timestamp.toString()+ BuildConfig.MARVEL_PRIVATE_KEY+ BuildConfig.MARVEL_PUBLIC_KEY)

        manageSub(
                service.getCharacters(timestamp.toString(), BuildConfig.MARVEL_PUBLIC_KEY, hash, defaultLimit)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( { c -> callback.endCallProgress(c)
                            originalList = c.data.results
                            countLimit = c.data.limit},
                                { e -> callback.endCallProgress(e)
                                    Log.e(MainActivity::class.java.simpleName, e.message)})
        )
    }

    fun loadMoreCharacters(callback: MainActivityViewModel, adapter: CharactersAdapter) {
        val timestamp = Date().time;
        val hash = UtilsHashMd5.md5(timestamp.toString()+ BuildConfig.MARVEL_PRIVATE_KEY+ BuildConfig.MARVEL_PUBLIC_KEY)

        manageSub(
                service.getCharacters(timestamp.toString(), BuildConfig.MARVEL_PUBLIC_KEY, hash, countLimit + defaultLimit)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( { c -> callback.endCallProgress(c)
                            updateIndexesForRequests(adapter, c)},
                                { e -> callback.endCallProgress(e)
                                    Log.e(MainActivity::class.java.simpleName, e.message)})
        )
    }

    fun updateIndexesForRequests(adapter: CharactersAdapter, response: Model.CharacterResponse) {
        adapter.characterResponse = response
        adapter.notifyItemRangeChanged(countLimit, countLimit + defaultLimit)
        originalList = response.data.results
        countLimit += defaultLimit
    }

}