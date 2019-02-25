package com.sango.lunchi.restaurantslist

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sango.core.model.Restaurant
import com.sango.lunchi.R
import com.sango.lunchi.databinding.ItemRestaurantBinding

class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun create(parent: ViewGroup): RestaurantViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_restaurant, parent, false)
            return RestaurantViewHolder(view)
        }
    }

    private val binding: ItemRestaurantBinding? = DataBindingUtil.bind(view)

    fun bind(restaurant: Restaurant?) {
        restaurant?.let {
            binding?.restaurant = restaurant
        }
    }
}