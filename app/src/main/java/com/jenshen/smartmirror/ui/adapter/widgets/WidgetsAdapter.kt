package com.jenshen.smartmirror.ui.adapter.widgets

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jenshen.smartmirror.R
import com.jenshen.smartmirror.data.firebase.DataSnapshotWithKey
import com.jenshen.smartmirror.data.firebase.model.widget.WidgetInfo
import com.jenshen.smartmirror.ui.holder.widgets.WidgetHolder

class WidgetsAdapter(private val context: Context, private val onItemClicked: (DataSnapshotWithKey<WidgetInfo>) -> Unit) : RecyclerView.Adapter<WidgetHolder>() {

    override fun getItemCount() = this.itemList.size

    private val itemList: MutableList<DataSnapshotWithKey<WidgetInfo>>

    init {
        this.itemList = mutableListOf<DataSnapshotWithKey<WidgetInfo>>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_widget, parent, false)
        return WidgetHolder(context, view)
    }

    override fun onBindViewHolder(holder: WidgetHolder, position: Int) {
        val model = itemList[position]
        holder.itemView.setOnClickListener { onItemClicked(model) }
        holder.bindInfo(model)
    }

    fun addModel(model: DataSnapshotWithKey<WidgetInfo>) {
        val position = itemList.indexOf(model)
        if (position != -1) {
            itemList.removeAt(position)
            itemList.add(position, model)
            notifyItemChanged(position)
        } else {
            itemList.add(0, model)
            notifyItemInserted(0)
        }
    }
}