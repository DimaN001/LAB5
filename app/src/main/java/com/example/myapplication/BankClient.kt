package com.example.myapplication
import java.io.Serializable

data class BankClient(
    val name: String,
    val email: String,
    val phone: String,
    val accountNumber: String,
    val balance: Double
) : Serializable
