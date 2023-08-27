package com.example.polychat

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WritePostActivity : AppCompatActivity() {

    data class Post(
        val title: String = "",
        val content: String = "",
        val notice: Boolean = false,
        val userID: Int = -1
    )

    private lateinit var database: FirebaseDatabase
    private lateinit var postRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        val userID = intent.getIntExtra("userID", -1)
        val postID = intent.getStringExtra("postID")
        val editMode = intent.getBooleanExtra("editMode", false)

        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)  //제목
        val editTextContent = findViewById<EditText>(R.id.editTextContent)  //내용
        val checkboxNotice = findViewById<CheckBox>(R.id.checkBoxNotice)   //공지체크박스
        val btnPost = findViewById<Button>(R.id.btnPost)    //'게시'

        database = FirebaseDatabase.getInstance()
        postRef = database.getReference("users").child(userID.toString()).child("post")

        if (editMode) {
            postRef.child(postID!!).get().addOnSuccessListener {
                val post = it.getValue(Post::class.java)
                editTextTitle.setText(post?.title)
                editTextContent.setText(post?.content)
                checkboxNotice.isChecked = post?.notice ?: false
            }.addOnFailureListener {
                Toast.makeText(this, "데이터 로드 실패", Toast.LENGTH_SHORT).show()
            }
        }

        btnPost.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            val isNotice = checkboxNotice.isChecked

            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val post = Post(title, content, isNotice, userID)

            if (editMode) {
                postRef.child(postID!!).setValue(post).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "게시글 수정 실패.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                postRef.push().setValue(post).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "게시글 작성 실패.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}




