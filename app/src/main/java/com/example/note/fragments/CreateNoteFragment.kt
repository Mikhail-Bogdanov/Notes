package com.example.note.fragments

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.speech.RecognizerIntent
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.note.R
import com.example.note.adapters.RecyclerViewAttachedAdapter
import com.example.note.databinding.FragmentCreateNoteBinding
import com.example.note.noteModels.NoteModel
import com.example.note.viewModels.CreateNoteViewModel
import com.example.note.workManager.MyWorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@AndroidEntryPoint
class CreateNoteFragment : Fragment() {

    private val createNoteViewModel: CreateNoteViewModel by viewModels()

    private var _navController: NavController? = null
    private val navController get() = _navController!!

    private var _binding: FragmentCreateNoteBinding? = null
    private val binding get() = _binding!!

    private var listAttached: ArrayList<String> = ArrayList()

    private var mediaRecorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var audioFilePath: String? = null
    private var isMicOn = false
    private var isPlaying: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNoteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _navController = Navigation.findNavController(view)

        createUI()

        sendRequest()
    }

    /**
     * обработка нажатий на фаб
     */
    private fun fabConfirmClickListener(){
        val noteModel = NoteModel(
            binding.etNoteNameCreate.text.toString(),
            binding.etNoteBodyCreate.text.toString(),
            listAttached,
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm")
            ).toString()
        )

        createNoteViewModel.insertNote(noteModel)

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

    /**
     * обработка нажатий на меню айтемы
     */
    private fun menuItemClick(menuItem: MenuItem){
        when(menuItem.itemId){
            R.id.attachImage -> fileFunction()
            R.id.speech_to_text -> speechFunction()
            R.id.attach_voice_message -> voiceMessageFunction()
        }
    }

    private fun sendRequest(){
        val data = Data.Builder()
            .putString("keyA", "value")
            .putInt("keyB", 3)
            .build()

        val oneRequest = OneTimeWorkRequestBuilder<MyWorkManager>()
            .addTag("oneReq")
            .setInputData(data)
            .setInitialDelay(1, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(requireContext())
            .enqueue(oneRequest)

    }

    /**
     * получение файла
     */
    private fun fileFunction(){
        val intentImage = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
            it.addCategory(Intent.CATEGORY_OPENABLE)
            it.type = "*/*"
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        contentFile.launch(intentImage)
    }

    /**
     * получение голосового ввода
     */
    private fun speechFunction(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also {
            it.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            it.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            it.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Dictate a note"
            )
        }
        contentS2T.launch(intent)
    }

    /**
     * включение или выключение рекордера аудио сообщения
     */
    private fun voiceMessageFunction(){
        if (!isMicOn)
            startRecordingAudioMessage()
        else
            stopRecordingAudioMessage()
    }

    private fun startRecordingAudioMessage(){
        if(checkPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                isMicOn = true
                val stringDateTime = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss")
                )
                audioFilePath = File(Environment.getExternalStorageDirectory()
                    .absolutePath + "/" + File.separator + "audio_$stringDateTime.3gp").absolutePath

                mediaRecorder = MediaRecorder(requireContext()).apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        setOutputFile(audioFilePath)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        try {
                            prepare()
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "There's some error: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        start()
                    }
            }
        } else requestPermissions()
    }

    /**
     * TODO хранить файлы в папке приложения internal storage
     * сделать папку для каждого пользователя с его ID и в этой папке
     * хранятся ГСки, ну типа
     * TODO с помощью work manager сделать сверку ID папок с ID заметок в базе данных
     */

    private fun stopRecordingAudioMessage() {
        isMicOn = false
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        listAttached.add(audioFilePath!!)
        (binding.rvAttachedCreate.adapter as RecyclerViewAttachedAdapter)
            .updateData(listAttached)
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
                Toast.makeText(
                    requireContext(),
                    "There's some error: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun stopPlayingAudioMessage(){
        isPlaying = false
        player?.release()
        player = null
    }

    /**
     * activity result for file
     */
    private val contentFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK){
            val uri: Uri = result.data?.data!!
            listAttached += uri.toString()
            requireActivity().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            (binding.rvAttachedCreate.adapter as RecyclerViewAttachedAdapter)
                .updateData(listAttached)
        }
    }

    /**
     * activity result for speech
     */
    @SuppressLint("SetTextI18n")
    private val contentS2T = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK || null != result.data){
            val resArray: ArrayList<String>? = result.data!!
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            val s = binding.etNoteBodyCreate.text.toString() + " "
            binding.etNoteBodyCreate.text = (s + resArray!![0]).toEditable()
        }
    }

    /**
     * отрисовка UI и навешивание клик листенеров
     */
    private fun createUI(){
        with(binding){
            fabConfirmCreate.setOnClickListener{
                fabConfirmClickListener()
            }
            botAppBarCreate.setOnMenuItemClickListener {
                menuItemClick(it)
                return@setOnMenuItemClickListener true
            }
            rvAttachedCreate.layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
            rvAttachedCreate.adapter = RecyclerViewAttachedAdapter(listAttached, ::handleClick)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _navController = null
    }

    private fun checkPermission(): Boolean {
        val result: Int = ContextCompat.checkSelfPermission(
            requireContext(),
            RECORD_AUDIO
        )
        val result1: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else false
        return result == PackageManager.PERMISSION_GRANTED && result1
    }

    private fun requestPermissions(){
        val micPerm: Int = ContextCompat.checkSelfPermission(
            requireContext(),
            RECORD_AUDIO
        )
        if(micPerm != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(RECORD_AUDIO),
                1
            )
        if(micPerm == PackageManager.PERMISSION_GRANTED)
            startActivity(Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
    }

    private fun String.toEditable() =  Editable.Factory.getInstance().newEditable(this)
}