package com.example.note.fragments

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.note.R
import com.example.note.adapters.RecyclerViewAttachedAdapter
import com.example.note.databinding.FragmentSecondBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SecondFragment : Fragment() {

    private var _navController: NavController? = null
    private val navController get() = _navController!!

    private var _secondFragmentArgs: SecondFragmentArgs? = null
    private val secondFragmentArgs get() = _secondFragmentArgs!!

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private var listAttached: List<String> = listOf()

    private var player: MediaPlayer? = null
    private var audioFilePath: String? = null
    private var isPlaying: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(layoutInflater, container, false)
        _secondFragmentArgs = SecondFragmentArgs.fromBundle(requireArguments())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _navController = Navigation.findNavController(view)

        listAttached = secondFragmentArgs.listUri.toList()

        with(binding){
            tvNoteNameSecond.text = secondFragmentArgs.name.toEditable()
            tvNoteBodySecond.text = secondFragmentArgs.body.toEditable()
            fabBackSecond.setOnClickListener{ fabBackClickListener() }
            rvAttachedSecond.layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
            rvAttachedSecond.adapter = RecyclerViewAttachedAdapter(listAttached, ::handleClick)
        }

    }

    /**
     * обработка нажатий на фаб
     */
    private fun fabBackClickListener(){
        navController.popBackStack()
    }

    /**
     * обработка обычного нажатия
     */
    private fun handleClick(pos: Int) : Int{
        val v = LayoutInflater.from(requireContext()).inflate(R.layout.image_dialog, null)
        val iv: ImageView = v.findViewById(R.id.iv_attached_big)
        val uri = Uri.parse(listAttached[pos])
        when(uri.pathSegments[1].slice(0..4)){
            "image" -> {
                iv.setImageURI(uri)
                MaterialAlertDialogBuilder(requireContext())
                    .setView(v)
                    .show()
            }
            else -> {
                when(uri.toString().slice(1..7)){
                    "storage" -> {
                        if (!isPlaying)
                            startPlayingAudioMessage(pos)
                        else
                            stopPlayingAudioMessage()
                    }
                    else -> Toast.makeText(
                        requireContext(),
                        "There's nothing yet :(",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return pos
    }

    private fun startPlayingAudioMessage(pos: Int){
        isPlaying = true
        audioFilePath = listAttached[pos]
        player = MediaPlayer().apply {
            try{
                setDataSource(audioFilePath)
                prepare()
                start()
            } catch (e: Exception) {
                Log.d("auf", e.localizedMessage)
            }
        }
    }

    private fun stopPlayingAudioMessage(){
        isPlaying = false
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _secondFragmentArgs = null
    }

    /**
     * конвертация String в Editable
     */
    private fun String.toEditable() =  Editable.Factory.getInstance().newEditable(this)
}