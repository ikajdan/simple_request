package com.example.simplerequest

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.simplerequest.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Request routine
    private fun requestRoutine() {
        var url = binding.urlBarInput.text.toString()
        if (url.isEmpty()) return

        hideKeyboard()

        binding.responseView.text = ""
        binding.progressBar.visibility = View.VISIBLE

        // Prepend with the protocol, if necessary
        if (!url.startsWith("https://", true) && !url.startsWith("http://", true)) {
            url = "https://$url"
        }

        // Instantiate the RequestQueue
        val queue = Volley.newRequestQueue(this)

        // Request a string response from the provided URL
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Hide the progress bar
                binding.progressBar.visibility = View.INVISIBLE

                // Show the response
                binding.responseView.text = response
            },
            {
                // Hide the progress bar
                binding.progressBar.visibility = View.INVISIBLE

                // Display the error message
                val msg = if (it.localizedMessage.isNullOrEmpty()) "Unknown Error" else it.localizedMessage
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.requestButton).show()
            }
        )

        // Add the request to the RequestQueue
        queue.add(stringRequest)
    }

    // Hide the keyboard
    private fun hideKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable request button only when URL input is not empty
        binding.urlBarInput.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                binding.requestButton.isEnabled = p0.toString().isNotBlank()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Stub
            }

            override fun afterTextChanged(p0: Editable?) {
                // Stub
            }
        })

        // Hide the keyboard when the input bar loses focus
        binding.responseView.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) hideKeyboard()
        }

        // Request with the enter keyboard button
        binding.urlBarInput.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                requestRoutine()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        binding.requestButton.setOnClickListener {
            requestRoutine()
        }
    }
}
