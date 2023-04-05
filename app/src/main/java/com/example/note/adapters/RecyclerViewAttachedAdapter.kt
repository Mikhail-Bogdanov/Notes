package com.example.note.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.databinding.RecyclerViewAttachedItemBinding

class RecyclerViewAttachedAdapter(
    var data: List<String>,
    val clickListener: (pos: Int) -> Int
) :
    RecyclerView.Adapter<RecyclerViewAttachedAdapter.MyViewHolder>(){

    inner class MyViewHolder(val binding: RecyclerViewAttachedItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RecyclerViewAttachedItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder){
            with(binding){
                if(data[0] == ""){
                    ivAttach.visibility = View.GONE
                } else {
                    val uri = Uri.parse(data[position])
                    when(uri.pathSegments[1].slice(0..4)){
                        "image" -> ivAttach.setImageURI(uri)
                        else -> {
                            when(uri.toString().slice(1..7)){
                                "storage" ->
                                    ivAttach.setImageResource(R.drawable.ic_baseline_audio_file_24)
                                else ->
                                    ivAttach.setImageResource(R.drawable.ic_baseline_rv_file_40)
                            }
                        }
                    }
                }
                ivAttach.setOnClickListener{
                    clickListener(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: List<String>){
        this.data = data
        this.notifyDataSetChanged()
    }
}