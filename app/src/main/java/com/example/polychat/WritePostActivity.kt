package com.example.polychat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WritePostActivity : AppCompatActivity() {
    data class Post(
        val title: String,
        val content: String,
        val notice: Boolean
    )   //게시글 정보 저장용 데이터클래스

    private lateinit var database: FirebaseDatabase
    private lateinit var postRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        // 사용자 ID와 게시글 ID를 가져옴
        val userID = intent.getIntExtra("userID", -1)
        val postID = intent.getStringExtra("postID")
        val editMode = intent.getBooleanExtra("editMode", false)

        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextContent = findViewById<EditText>(R.id.editTextContent)
        val btnPost = findViewById<Button>(R.id.btnPost)

        // Firebase초기화
        database = FirebaseDatabase.getInstance()
        postRef = database.getReference("users").child(userID.toString()).child("post")


        // 수정 모드일 경우 기존 게시글 정보를 가져와 화면에 표시
        if (editMode) {
            postRef.child(postID!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val post = dataSnapshot.getValue(Post::class.java)
                    editTextTitle.setText(post?.title)
                    editTextContent.setText(post?.content)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@WritePostActivity, "데이터 로드 실패: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        //'게시'
        btnPost.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            // 제목과 내용이 입력되지 않았을 경우 메시지 표시
            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val post = Post(title, content, false) // // 공지사항 체크 추가

            // 수정 모드일 경우 게시글을 수정
            if (editMode) {
                postRef.child(postID!!).setValue(post).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "게시글 수정 실패.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {    //수정 모드가 아니면 게시글 작성
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



