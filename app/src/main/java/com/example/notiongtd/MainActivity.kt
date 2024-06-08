package com.example.notiongtd

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.notiongtd.services.NotionService

import com.example.notiongtd.ui.theme.NotionGTDTheme

import java.io.IOException

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val inputGtd = findViewById<EditText>(R.id.inputGtd)
        val addButton = findViewById<Button>(R.id.addButton)

        addButton.setOnClickListener {
            val inputText = inputGtd.text.toString()
            addToInboxGtd(inputText)
            inputGtd.text.clear()
        }

        inputGtd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addButton.isEnabled = !s.isNullOrEmpty()
            }
        })
    }

    private fun announce(info: String, duration: Int = Toast.LENGTH_LONG) {
        this@MainActivity.runOnUiThread {
            Toast.makeText(this@MainActivity, info, duration).show()
        }
    }

    private fun addToInboxGtd(item: String) {
        announce("Sending item to GTD Inbox...",Toast.LENGTH_SHORT)

        val client = OkHttpClient()
        val notionService = NotionService()

        client.newCall(notionService.updateNotionBlock(item)).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                println("[END BAD 0] addToInboxGtd ${e.message}")
                announce("Request Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (!response.isSuccessful) {
                        println("[END BAD] addToInboxGtd msg ${response.body?.string()}")
                        announce("Error: ${response.code}")
                    }

                    println("[END OK] addToInboxGtd ${response.body?.string()}")
                    announce("Element added successfully")
                }
            }
        })

    }
}