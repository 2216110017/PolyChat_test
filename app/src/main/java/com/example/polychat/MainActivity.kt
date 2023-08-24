package com.example.polychat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var stuNameTextView: TextView
    private lateinit var stuNumTextView: TextView
    private lateinit var departmentTextView: TextView
    private lateinit var boardButton: Button
    private lateinit var chatListButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stuNameTextView = findViewById(R.id.stuNameTextView)
        stuNumTextView = findViewById(R.id.stuNumTextView)
        departmentTextView = findViewById(R.id.departmentTextView)
        boardButton = findViewById(R.id.boardButton)
        chatListButton = findViewById(R.id.chatListButton)

        val stuNum = intent.getStringExtra("stuNum")
        val stuName = intent.getStringExtra("stuName")
        val department = intent.getStringExtra("department")

        stuNameTextView.text = stuName
        stuNumTextView.text = stuNum
        departmentTextView.text = department

        boardButton.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }

        chatListButton.setOnClickListener {
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
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent) // 로그아웃 후 LopginActivity로 ㅇ ㅣ동
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