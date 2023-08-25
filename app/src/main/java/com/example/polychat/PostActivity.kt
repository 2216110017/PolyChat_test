package com.example.polychat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase



class PostActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var noticeCheckBox: CheckBox
    private lateinit var postButton: Button
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        noticeCheckBox = findViewById(R.id.noticeCheckBox)
        postButton = findViewById(R.id.postButton)

        postButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val isNotice = noticeCheckBox.isChecked

            // Firebase에 게시물 저장
            savePostToFirebase(title, content, isNotice)
        }
    }

    private fun savePostToFirebase(title: String, content: String, isNotice: Boolean) {
        val database = FirebaseDatabase.getInstance()
        val postsRef = database.getReference("posts") // "posts" 노드에 게시물 저장

        val postId = postsRef.push().key // 게시물의 고유 ID 생성

        if (postId != null) {
            val timestamp = System.currentTimeMillis() // 현재 시간 정보
            val post = Post(postId, title, content, getCurrentUser(), timestamp)

            // 게시물 정보를 Firebase Realtime Database에 저장
            postsRef.child(postId).setValue(post)
                .addOnSuccessListener {
                    showToast("게시물이 저장되었습니다.")
                    // 게시물 저장 성공 후 BoardActivity로 이동
                    val intent = Intent(this, BoardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    showToast("게시물 저장에 실패했습니다.")
                }
        }
    }

    private fun getCurrentUser(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users/$userId/stuName") // "path_to_users"는 실제 경로로 변경해야 합니다.

            val stuName = userRef.get().result?.getValue(String::class.java)
            return stuName ?: "Unknown"
        }
        return "Unknown"
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent) // 로그아웃 후 LopginActivity로 ㅇ ㅣ동
                true
            }

            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}