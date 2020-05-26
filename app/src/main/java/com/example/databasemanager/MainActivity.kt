package com.example.databasemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.databasemanager.database.MyDBHandler
import com.example.databasemanager.database.Tables.User

class MainActivity : AppCompatActivity() {

    var dbHandler: MyDBHandler? = null

    private lateinit var loginButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginButton = findViewById(R.id.loginButton)
        usernameEditText = findViewById(R.id.passwordEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        dbHandler = MyDBHandler(this)

        loginButton.setOnClickListener {
            val user: User = User(0, usernameEditText.text.toString(), passwordEditText.text.toString())
            var success: Boolean

            var users = dbHandler!!.login(user)

            if (users.isNotEmpty()) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainMenu::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Username or Password incorrect", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
