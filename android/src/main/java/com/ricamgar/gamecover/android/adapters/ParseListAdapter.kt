/*
 * Copyright (c) 2015. Make It Real S.L.
 */

package com.ricamgar.gamecover.android.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.common.collect.Collections2
import com.ricamgar.gamecover.android.R
import com.ricamgar.gamecover.android.models.GameItem
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Adapter for the list with the objects from Parse.
 * To download the images uses an AsyncTask.
 */
class ParseListAdapter(val listener: ParseListAdapter.ItemClickListener) : RecyclerView.Adapter<ParseListAdapter.ViewHolder>() {

    private var context: Context? = null
    private var original: List<GameItem> = ArrayList()
    private var objects: List<GameItem> = ArrayList()

    fun addItems(results: List<GameItem>) {
        this.original = results
        this.objects = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.games_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gameItem = objects[position]

        holder.itemTitle.text = gameItem.title
        holder.itemDev.text = gameItem.developer
        holder.itemImage.visibility = View.GONE
        holder.itemProgress.visibility = View.VISIBLE
        Picasso.with(context).load(gameItem.image.url).into(holder.itemImage, object : Callback {
            override fun onSuccess() {
                holder.itemProgress.visibility = View.GONE
                holder.itemImage.visibility = View.VISIBLE
            }

            override fun onError() {
                // not needed
            }
        })
        holder.itemInstalled.visibility = View.GONE
        if (gameItem.packageId != null) {
            val intent = context!!.packageManager.getLaunchIntentForPackage(gameItem.packageId)
            if (intent != null) {
                holder.itemInstalled.visibility = View.VISIBLE
            }
        }

        holder.itemView.setOnClickListener { v -> listener.onItemSelected(gameItem.packageId) }
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    fun filter(input: String) {
        val searchStr = input.trim { it <= ' ' }.toLowerCase()
        objects = ArrayList(Collections2.filter(original) { `object` -> `object`.title.trim { it <= ' ' }.toLowerCase().contains(searchStr) })
        notifyDataSetChanged()
    }

    fun orderByDate() {
        Collections.sort(objects) {
            gameItem1, gameItem2 ->
            -gameItem1.createdAt.compareTo(gameItem2.createdAt)
        }
        notifyDataSetChanged()
    }

    fun orderByName() {
        Collections.sort(objects) { gameItem1, gameItem2 -> gameItem1.title.compareTo(gameItem2.title) }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTitle: TextView
        val itemDev: TextView
        val itemImage: ImageView
        val itemInstalled: ImageView
        val itemProgress: View

        init {
            itemTitle = view.findViewById(R.id.games_item_title) as TextView
            itemDev = view.findViewById(R.id.games_item_dev) as TextView
            itemImage = view.findViewById(R.id.games_item_image) as ImageView
            itemInstalled = view.findViewById(R.id.games_item_installed) as ImageView
            itemProgress = view.findViewById(R.id.games_item_progress)
        }
    }

    /**
     * Listener for item click events
     */
    interface ItemClickListener {
        fun onItemSelected(packageId: String?)
    }
}
