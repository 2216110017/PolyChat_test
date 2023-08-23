package com.example.polychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var profileNameTextView: TextView
    private lateinit var profileStuNumTextView: TextView
    private lateinit var profileDepartmentTextView: TextView
    private lateinit var boardButton: Button
    private lateinit var studentsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileNameTextView = findViewById(R.id.profileNameTextView)
        profileStuNumTextView = findViewById(R.id.profileStuNumTextView)
        profileDepartmentTextView = findViewById(R.id.profileDepartmentTextView)
        boardButton = findViewById(R.id.boardButton)
        studentsButton = findViewById(R.id.studentsButton)

        boardButton.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }

        studentsButton.setOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                return true
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}