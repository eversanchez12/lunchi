package com.sango.lunchi.restaurantslist

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.sango.core.model.Restaurant

class RestaurantListAdapter() : PagedListAdapter<Restaurant, RestaurantViewHolder>(STORE_COMPARATOR) {

    companion object {
        val STORE_COMPARATOR = object : DiffUtil.ItemCallback<Restaurant>() {
            override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RestaurantViewHolder.create(parent)

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        if (itemCount != 0) {
            val restaurantItem = getItem(position)
            if (restaurantItem != null) {
                holder.bind(restaurantItem)
            }
        }
    }
}