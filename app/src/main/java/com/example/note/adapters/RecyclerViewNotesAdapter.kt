package com.example.note.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.note.databinding.RecyclerViewNotesItemBinding
import com.example.note.noteModels.NoteModel

class RecyclerViewNotesAdapter(
    var data: List<NoteModel>,
    val clickListener: (pos: Int) -> Int
) :
    RecyclerView.Adapter<RecyclerViewNotesAdapter.MyViewHolder>(){

        inner class MyViewHolder(val binding: RecyclerViewNotesItemBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = RecyclerViewNotesItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder){
            with(binding){
                tvItemNameRvItem.text = data[position].name
                tvItemTimeRvItem.text = data[position].dateTime
                cvItem.setOnClickListener{
                    clickListener(position)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: List<NoteModel>){
        this.data = data
        this.notifyDataSetChanged()
    }
}