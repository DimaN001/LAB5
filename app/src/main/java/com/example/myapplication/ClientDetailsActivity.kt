package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ClientDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_details)

        // Отримання даних про клієнта з Intent
        val client: BankClient = intent.getSerializableExtra("client") as BankClient

        // Відображення даних про клієнта
        val detailsTextView: TextView = findViewById(R.id.detailsTextView)
        detailsTextView.text = """
            Name: ${client.name}
            Email: ${client.email}
            Phone: ${client.phone}
            Account Number: ${client.accountNumber}
            Balance: ${client.balance}
        """.trimIndent()


        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }


}
