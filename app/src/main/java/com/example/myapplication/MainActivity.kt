package com.example.myapplication
import java.util.Locale
import android.app.*
import android.widget.TextView
import android.content.res.Configuration
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Spinner


class MainActivity : AppCompatActivity() {

    private lateinit var clientsListView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var clients: MutableList<BankClient>
    private lateinit var languageSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        languageSpinner = findViewById(R.id.languageSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.language_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            languageSpinner.adapter = adapter
        }

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> setLocale("en")
                    1 -> setLocale("uk")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        clientsListView = findViewById(R.id.clientsListView)

        clients = mutableListOf(
            BankClient(
                "Негер Дмитро",
                "dimaneger@icloud.com",
                "+380992728517",
                "123456789",
                1000.0
            ),
            BankClient("Jane Smith", "jane.smith@example.com", "+9876543210", "987654321", 2000.0)
        )

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, clients.map { it.name })
        clientsListView.adapter = adapter

        registerForContextMenu(clientsListView)

        clientsListView.setOnItemClickListener { _, _, position, _ ->
            showDetails(position)
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale

        resources.updateConfiguration(config, resources.displayMetrics)

        // Оновлення тексту у textView
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = getString(R.string.long_click_message)


        // Зберегти вибрану мову
        val prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()


    }




    private fun showDetails(position: Int) {
        val client = clients[position]



        val intent = Intent(this, ClientDetailsActivity::class.java)
        intent.putExtra("client", client)
        startActivity(intent)
    }


    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val position = info.position

        when (item.itemId) {
            R.id.menuCall -> callClient(position)
            R.id.menuEmail -> emailClient(position)
            R.id.menuAdd -> addClient()
            R.id.menuEdit -> editClient(position)
            R.id.menuDelete -> deleteClient(position)
            R.id.menuDetails -> showDetails(position)
        }

        return super.onContextItemSelected(item)
    }

    private fun callClient(position: Int) {
        val phoneNumber = clients[position].phone
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
    }

    private fun emailClient(position: Int) {
        val email = clients[position].email
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
        startActivity(intent)
    }

    private fun addClient() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_client, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.add_client))
        alertDialogBuilder.setView(dialogView)

        alertDialogBuilder.setPositiveButton(getString(R.string.add)) { _, _ ->
            val nameEditText = dialogView.findViewById<EditText>(R.id.editTextNames)
            val emailEditText = dialogView.findViewById<EditText>(R.id.editTextEmail)
            val phoneEditText = dialogView.findViewById<EditText>(R.id.editTextPhone)
            val accountNumberEditText = dialogView.findViewById<EditText>(R.id.editTextAccountNumber)
            val balanceEditText = dialogView.findViewById<EditText>(R.id.editTextBalance)

            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val accountNumber = accountNumberEditText.text.toString()
            val balance = balanceEditText.text.toString().toDoubleOrNull() ?: 0.0

            val newClient = BankClient(name, email, phone, accountNumber, balance)
            clients.add(newClient)
            adapter.add(name)
            adapter.notifyDataSetChanged()
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        alertDialogBuilder.create().show()
    }

    private fun editClient(position: Int) {
        val client = clients[position]

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_client, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.edit_client))
        alertDialogBuilder.setView(dialogView)

        // Initialize EditTexts with current client data
        val nameEditText = dialogView.findViewById<EditText>(R.id.editTextName)
        val emailEditText = dialogView.findViewById<EditText>(R.id.editTextEmail)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.editTextPhone)
        val accountNumberEditText = dialogView.findViewById<EditText>(R.id.editTextAccountNumber)
        val balanceEditText = dialogView.findViewById<EditText>(R.id.editTextBalance)

        nameEditText.setText(client.name)
        emailEditText.setText(client.email)
        phoneEditText.setText(client.phone)
        accountNumberEditText.setText(client.accountNumber)
        balanceEditText.setText(client.balance.toString())

        alertDialogBuilder.setPositiveButton(getString(R.string.save)) { _, _ ->
            // Update client data with edited values
            val editedName = nameEditText.text.toString()
            val editedEmail = emailEditText.text.toString()
            val editedPhone = phoneEditText.text.toString()
            val editedAccountNumber = accountNumberEditText.text.toString()
            val editedBalance = balanceEditText.text.toString().toDoubleOrNull() ?: 0.0

            val editedClient = BankClient(editedName, editedEmail, editedPhone, editedAccountNumber, editedBalance)

            // Update the client in the list
            clients[position] = editedClient

            // Update the adapter
            adapter.clear()
            adapter.addAll(clients.map { it.name })
            adapter.notifyDataSetChanged()
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        alertDialogBuilder.create().show()
    }

    private fun deleteClient(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.delete_client))
        alertDialogBuilder.setMessage(getString(R.string.confirm_delete_client))

        alertDialogBuilder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            // Видалення клієнта за певним індексом (position)
            clients.removeAt(position)

            // Оновлення адаптеру
            adapter.clear()
            adapter.addAll(clients.map { it.name })
            adapter.notifyDataSetChanged()
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.cancel()
        }

        alertDialogBuilder.create().show()
    }
}
