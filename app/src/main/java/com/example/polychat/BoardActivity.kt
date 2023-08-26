package com.example.polychat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BoardActivity : AppCompatActivity() {

    private lateinit var postsListView: ListView
    private lateinit var writePostButton: Button
    private lateinit var postAdapter: PostAdapter
    private lateinit var postsList: MutableList<Post>
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        postsList = mutableListOf()

        postsListView = findViewById(R.id.postsListView)
        writePostButton = findViewById(R.id.writePostButton)


        // Firebase에서 게시글 목록 가져오기
        val userID = intent.getStringExtra("userID")

        if (userID != null) {
            val userPostsRef = FirebaseDatabase.getInstance().getReference("users/$userID/posts")
            userPostsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    postsList.clear()
                    for (postSnapshot in dataSnapshot.children) {
                        val post = postSnapshot.getValue(Post::class.java)
                        post?.let { postsList.add(it) }
                    }
                    val postAdapter = PostAdapter(this@BoardActivity, postsList)
                    postsListView.adapter = postAdapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 에러 처리
                }
            })
        }

        writePostButton.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            intent.putExtra("userID", userID)
            resultLauncher.launch(intent)
        }

        postsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedPost = postsList[position]
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("userID", userID)
            intent.putExtra("postTitle", selectedPost.title)
            intent.putExtra("postContent", selectedPost.content)
            startActivity(intent)
            // 선택한 게시물 보기 PostDetailActivity로 이동
        }

        val postAdapter = PostAdapter(this, postsList)
        postsListView.adapter = postAdapter

        postsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedPost = postsList[position]
            val postRef = FirebaseDatabase.getInstance().getReference("posts/${selectedPost.postId}")
            postRef.removeValue() // Firebase Realtime Database에서 게시글 삭제
            postsList.removeAt(position)  // 게시글 목록에서도 삭제하여 갱신
            postAdapter.notifyDataSetChanged()
        }

    }
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val title = data?.getStringExtra("postTitle")
            val content = data?.getStringExtra("postContent")
            val stuName = currentUser.stuName // 현재 사용자의 이름을 가져옴
            val timestamp = System.currentTimeMillis()

            val userID = intent.getStringExtra("userID")
            if (userID != null) {
                val userPostsRef =
                    FirebaseDatabase.getInstance().getReference("users/$userID/posts")
                val newPostRef = userPostsRef.push() // 새로운 게시글을 위한 참조 생성
                val newPost = Post(newPostRef.key!!, title!!, content!!, stuName, timestamp)
                newPostRef.setValue(newPost) // Firebase Realtime Database에 게시글 저장

                // 게시글 목록에 추가하여 갱신
                postsList.add(newPost)
                postAdapter.notifyDataSetChanged()
            }
        }
    }

    //메뉴
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