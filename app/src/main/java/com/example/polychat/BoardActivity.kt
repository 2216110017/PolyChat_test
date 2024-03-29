package com.example.polychat

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BoardActivity : AppCompatActivity() {

    // Firebase 데이터베이스 참조 변수 선언
    private lateinit var database: FirebaseDatabase
    private lateinit var postsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        //뒤로 버튼
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()  // 현재 액티비티를 종료하고 이전 액티비티로 돌아갑니다.
        }

        // 로그인한 사용자의 ID 가져오기
        val userID = intent.getIntExtra("userID", -1)

        // UI 요소 참조 가져오기
        val listViewPosts = findViewById<ListView>(R.id.lvPosts)
        val btnWritePost = findViewById<Button>(R.id.btnWritePost)

        // Firebase 초기화
        database = FirebaseDatabase.getInstance()
        postsRef = database.getReference("users").child(userID.toString()).child("post")

        // 게시글 작성 버튼 클릭 리스너 설정
        btnWritePost.setOnClickListener {
            // 게시글 작성 화면으로 이동
            val intent = Intent(this, WritePostActivity::class.java)
            intent.putExtra("userID", userID) // 사용자의 ID를 함께 전달
            startActivity(intent)
        }

        // 게시글의 unique key를 저장하기 위한 리스트
        val postKeys = mutableListOf<String>()

        // Firebase에서 게시글 목록 가져오기
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val noticePostsList = mutableListOf<String>()
                val normalPostsList = mutableListOf<String>()
                postKeys.clear() // 리스트 초기화

                // 각 게시글에 대해
                for (postSnapshot in dataSnapshot.children) {
                    val post = postSnapshot.getValue(WritePostActivity.Post::class.java)
                    if (post?.notice == true) {
                        noticePostsList.add(post.title ?: "No title")
                    } else {
                        normalPostsList.add(post?.title ?: "No title")
                    }
                    postKeys.add(postSnapshot.key ?: "") // 게시글의 unique key 추가
                }

                val mergedPostsList = noticePostsList + normalPostsList
                // 게시글 목록을 화면에 표시하기 위한 ArrayAdapter 설정
                val adapter = object : ArrayAdapter<String>(this@BoardActivity, android.R.layout.simple_list_item_1, mergedPostsList) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getView(position, convertView, parent)
                        val textView = view.findViewById<TextView>(android.R.id.text1)
                        if (position < noticePostsList.size) {
                            textView.setTypeface(textView.typeface, Typeface.BOLD)
                            textView.setBackgroundColor(Color.LTGRAY)
                        } else {
                            textView.setTypeface(null, Typeface.NORMAL)
                            textView.setBackgroundColor(Color.TRANSPARENT)
                        }
                        return view
                    }
                }
                listViewPosts.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터 로드 실패 시 에러 처리
            }
        })

        // 게시글 목록의 아이템 클릭 리스너 설정
        listViewPosts.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this@BoardActivity, PostDetailActivity::class.java)
            intent.putExtra("postID", postKeys[position]) // 선택된 게시글의 unique key 전달
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
    }
}
