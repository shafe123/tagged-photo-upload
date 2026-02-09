package com.example.taggedphotoupload

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button

class SetupActivity : AppCompatActivity() {
    
    private lateinit var configManager: ConfigManager
    private lateinit var accountNameInput: TextInputEditText
    private lateinit var accountKeyInput: TextInputEditText
    private lateinit var containerNameInput: TextInputEditText
    private lateinit var saveButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        
        configManager = ConfigManager(this)
        
        // Initialize views
        accountNameInput = findViewById(R.id.accountNameInput)
        accountKeyInput = findViewById(R.id.accountKeyInput)
        containerNameInput = findViewById(R.id.containerNameInput)
        saveButton = findViewById(R.id.saveButton)
        
        // Load existing configuration
        loadConfiguration()
        
        // Set up save button
        saveButton.setOnClickListener {
            saveConfiguration()
        }
    }
    
    private fun loadConfiguration() {
        accountNameInput.setText(configManager.getAccountName() ?: "")
        accountKeyInput.setText(configManager.getAccountKey() ?: "")
        containerNameInput.setText(configManager.getContainerName() ?: "")
    }
    
    private fun saveConfiguration() {
        val accountName = accountNameInput.text?.toString()?.trim() ?: ""
        val accountKey = accountKeyInput.text?.toString()?.trim() ?: ""
        val containerName = containerNameInput.text?.toString()?.trim() ?: ""
        
        if (accountName.isEmpty() || accountKey.isEmpty() || containerName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        configManager.saveAzureConfig(accountName, accountKey, containerName)
        Toast.makeText(this, R.string.config_saved, Toast.LENGTH_SHORT).show()
        finish()
    }
}
