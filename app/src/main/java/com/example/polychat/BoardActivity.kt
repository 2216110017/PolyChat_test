package com.example.polychat

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BoardActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var postsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        val userID = intent.getIntExtra("userID", -1)

        val listViewPosts = findViewById<ListView>(R.id.lvPosts)
        val btnWritePost = findViewById<Button>(R.id.btnWritePost)

        database = FirebaseDatabase.getInstance()
        postsRef = database.getReference("users").child(userID.toString()).child("post")

        btnWritePost.setOnClickListener {
            // 게시글 작성 화면으로 이동
            val intent = Intent(this, WritePostActivity::class.java)
            intent.putExtra("userID", userID) // 사용자의 ID를 함께 전달
            startActivity(intent)
        }

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val postsList = mutableListOf<String>()
                for (postSnapshot in dataSnapshot.children) {
                    val post = postSnapshot.getValue(WritePostActivity.Post::class.java)
                    postsList.add(post?.title ?: "No title")
                }

                val adapter = ArrayAdapter(this@BoardActivity, android.R.layout.simple_list_item_1, postsList)
                listViewPosts.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}


