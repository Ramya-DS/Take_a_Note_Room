package com.example.take_a_note_room

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

class ColorAdapter(
    val mOnColorSelectedListener: OnColorSelectedListener,
    val context: WeakReference<Context>
) :
    RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    inner class ColorViewHolder(val colorView: View) : RecyclerView.ViewHolder(colorView),
        View.OnClickListener {
        init {
            colorView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mOnColorSelectedListener.onColorSelected(ColorPickerFragment.value!![adapterPosition].color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder =
        ColorViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.color_box,
                parent,
                false
            )
        )

    override fun getItemCount() = ColorPickerFragment.value?.size ?: -1

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.colorView.setBackgroundColor(
            ContextCompat.getColor(
                context.get()!!,
                ColorPickerFragment.value!![position].color
            )
        )
    }


}