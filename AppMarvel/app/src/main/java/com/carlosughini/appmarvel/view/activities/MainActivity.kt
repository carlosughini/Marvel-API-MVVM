package com.carlosughini.appmarvel.view.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.view.Menu
import android.view.MenuInflater
import com.carlosughini.appmarvel.R
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.view.adapters.CharactersAdapter
import com.carlosughini.appmarvel.databinding.ActivityMainBinding
import com.carlosughini.appmarvel.utils.InfiniteScrollListener
import com.carlosughini.appmarvel.viewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainViewModel.MainActivityViewModel {

    lateinit var mainViewModel: MainViewModel
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = MainViewModel(this)
        binding.viewmodel = mainViewModel
        setSupportActionBar(binding.toolbar)

        mainViewModel.loadCharactersList(this)

        val linearLayout = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayout
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.addOnScrollListener(InfiniteScrollListener({ mainViewModel.loadMoreCharacters(this, recyclerView.adapter as CharactersAdapter) }, linearLayout))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_activity_main, menu)

        val item = menu?.findItem(R.id.search)
        searchView = MenuItemCompat.getActionView(item) as SearchView

        return super.onCreateOptionsMenu(menu)

    }

    private fun charactersList(characterResponse: Model.CharacterResponse) {
        val adapter = CharactersAdapter(characterResponse)
        recyclerView.adapter = adapter
        searchView.setOnQueryTextListener(mainViewModel.getOnQueryTextChange(recyclerView.adapter as CharactersAdapter));

    }

    override fun endCallProgress(any: Any?) {
        if (any is Throwable) {
            println("Error: " + any.message)
        } else if (any is Model.CharacterResponse) {
            this.charactersList(any)
        }
    }

    override fun onDestroy() {
        mainViewModel.unsubscribe()
        super.onDestroy()
    }
}
