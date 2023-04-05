package com.example.note.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.note.R
import com.example.note.adapters.RecyclerViewNotesAdapter
import com.example.note.databinding.FragmentMainBinding
import com.example.note.feedback.classes.Event
import com.example.note.noteModels.NoteModel
import com.example.note.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()

    private var _navController: NavController? = null
    private val navController get() = _navController!!

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var listNotes: ArrayList<NoteModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _navController = Navigation.findNavController(view)

        createUI()

    }

    override fun onStart() {
        super.onStart()
        subscribeStateFlowNotes()
        subscribeStateFlowLoader()
        subscribeEventsFlow()
        mainViewModel.updateSFNotes()
    }

    private fun fabClickListener() =
        navController.navigate(R.id.createNoteFragment)

    private fun handleClick(pos: Int) : Int{
        val action = MainFragmentDirections
            .actionMainFragmentToSecondFragment(
                listNotes[pos].listUri.toTypedArray(),
                listNotes[pos].name,
                listNotes[pos].text
            )
        navController.navigate(action)
        return pos
    }

    /**
     * обработка нажатий на меню айтемы
     */
    private fun menuItemClick(menuItem: MenuItem){
        when(menuItem.itemId){
            R.id.clear_database ->
                mainViewModel.clearDatabase(listNotes)
        }
    }

    /**
     * обработка свапа rv айтема
     */
    private val simpleItem = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) =
            mainViewModel.deleteNote(listNotes[viewHolder.absoluteAdapterPosition])
    }

    /**
     * подписка на обновление заметок
     */
    private fun subscribeStateFlowNotes() {
        lifecycleScope.launch {
            mainViewModel.stateFlowNotes.collect {
                if (it != null) {
                    listNotes = it
                    (binding.rvNotes.adapter as RecyclerViewNotesAdapter)
                        .updateData(listNotes)
                    Log.d("auf", "database on started: $it")
                }
            }
        }
    }

    /**
     * подписка на обновление лоадера
     */
    private fun subscribeStateFlowLoader(){
        lifecycleScope.launch{
            mainViewModel.stateFlowLoader.collect{
                if(it != null){
                    when(it){
                        true -> {
                            binding.rvNotes.visibility = View.GONE
                            binding.loadingProgressBar.visibility = View.VISIBLE
                        }
                        false -> {
                            binding.loadingProgressBar.visibility = View.GONE
                            binding.rvNotes.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    /**
     * подписка на ивенты вью модели
     */
    private fun subscribeEventsFlow(){
        mainViewModel.eventsFlow.onEach {
            when(it){
                is Event.ShowSnackBar -> {
                    Snackbar.make(requireView(), it.text, Snackbar.LENGTH_SHORT).also { snackbar ->
                        snackbar.anchorView = binding.fabNewMain
                        snackbar.show()
                    }
                }
                is Event.ShowToast -> {
                    Log.d("auf", it.text)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

    }

    /**
     * отрисовка интерфейса и установка обработчиков нажатий
     */
    private fun createUI(){
        with(binding){
            with(rvNotes){
                ItemTouchHelper(simpleItem).attachToRecyclerView(this)
                layoutManager = LinearLayoutManager(context)
                adapter = RecyclerViewNotesAdapter(listNotes, ::handleClick)
            }
            fabNewMain.setOnClickListener{
                fabClickListener()
            }
            botAppBarMain.setOnMenuItemClickListener {
                menuItemClick(it)
                return@setOnMenuItemClickListener true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _navController = null
    }
}