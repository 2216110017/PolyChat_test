package com.example.polychat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PostDetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        titleTextView = findViewById(R.id.titleTextView)
        contentTextView = findViewById(R.id.contentTextView)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)

        editButton.setOnClickListener {
            // TODO: PostEditActivity로 이동
        }

        deleteButton.setOnClickListener {
            // TODO: 게시물 삭제하고 BoardActivity로 다시 이동
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