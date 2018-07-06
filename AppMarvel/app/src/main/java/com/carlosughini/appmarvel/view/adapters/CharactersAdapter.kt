package com.carlosughini.appmarvel.view.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.carlosughini.appmarvel.R
import com.carlosughini.appmarvel.model.Model
import com.carlosughini.appmarvel.databinding.ItemCharacterBinding
import com.carlosughini.appmarvel.viewModel.CharacterViewModel

class CharactersAdapter(var characterResponse: Model.CharacterResponse) : RecyclerView.Adapter<CharactersAdapter.ItemCharacterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCharacterViewHolder {
        val itemCharacterBinding = DataBindingUtil.inflate<ItemCharacterBinding>(LayoutInflater.from(parent.context), R.layout.item_character, parent, false)

        return ItemCharacterViewHolder(itemCharacterBinding)

    }

    override fun onBindViewHolder(holder: ItemCharacterViewHolder, position: Int) {
        holder.bindItemCharacter(characterResponse.data.results[position])
        setAnimation(holder.itemView)
    }

    override fun getItemCount() = characterResponse.data.results.size

    class ItemCharacterViewHolder(val binding: ItemCharacterBinding) : RecyclerView.ViewHolder(binding.cardView) {
        fun bindItemCharacter(character: Model.Character) {
            val viewmodel = CharacterViewModel(itemView.context, character)
            binding.cardView.setOnClickListener { viewmodel.openDetailActivity() }
            binding.viewmodel = viewmodel
        }

    }

    private fun setAnimation(viewToAnimate: View) {
        if (viewToAnimate.animation == null) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.animation = animation
        }
    }

}