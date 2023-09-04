package com.mey.imagetotext.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mey.imagetotext.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    private lateinit var textRecognizer: TextRecognizer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        handleClickEvents()
    }


    private fun handleClickEvents() {
        binding.apply {
            cardPickImage.setOnClickListener {
                pickImage()
            }
            cardCopy.setOnClickListener {
                copyTextToClipboard()
            }
            cardClear.setOnClickListener {
                clearResultText()
            }

        }
    }

    private fun clearResultText() {
        binding.tvResult.text = ""
    }

    private fun copyTextToClipboard() {
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Data", binding.tvResult.text.toString())
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun pickImage() {
        ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                recognizeText(uri)
                Toast.makeText(requireContext(), "Image Selected", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Image not Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recognizeText(uri: Uri) {
        try {
            val inputImage = InputImage.fromFilePath(requireContext(), uri)
            val result: Task<Text> = textRecognizer.process(inputImage).addOnSuccessListener { text ->
                Log.e("TAG", "text : $text")
                val recognizedText = text.text
                binding.tvResult.text = recognizedText
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun initialize() {
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}