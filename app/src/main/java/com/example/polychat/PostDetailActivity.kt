package com.example.polychat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostDetailActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var postRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        // 사용자 ID와 게시글 ID를 가져오기
        val userID = intent.getIntExtra("userID", -1)
        val postID = intent.getStringExtra("postID")

        val tvPostTitle = findViewById<TextView>(R.id.tvPostTitle)
        val tvPostContent = findViewById<TextView>(R.id.tvPostContent)
        val btnEditPost = findViewById<Button>(R.id.btnEditPost)
        val btnDeletePost = findViewById<Button>(R.id.btnDeletePost)

        // Firebase초기화
        database = FirebaseDatabase.getInstance()
        // 사용자 게시글 데이터 참조 가져오기
        postRef = database.getReference("users").child(userID.toString()).child("post").child(postID!!)

        // 게시글 데이터를 가져와 화면에 표시
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(WritePostActivity.Post::class.java)
                tvPostTitle.text = post?.title
                tvPostContent.text = post?.content
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        //'수정'버튼
        btnEditPost.setOnClickListener {
            // 게시글 수정 화면으로 이동
            val intent = Intent(this@PostDetailActivity, WritePostActivity::class.java)
            intent.putExtra("userID", userID)
            intent.putExtra("postID", postID)
            intent.putExtra("editMode", true) // 수정 모드로 실행
            startActivity(intent)
        }
        //'삭제'
        btnDeletePost.setOnClickListener {
            postRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@PostDetailActivity, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                    finish() // 현재 화면 종료 및 이전 화면으로 이동
                } else {
                    Toast.makeText(this@PostDetailActivity, "게시글 삭제 실패.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}