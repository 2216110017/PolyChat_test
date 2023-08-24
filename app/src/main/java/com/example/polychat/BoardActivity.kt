package com.example.polychat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class BoardActivity : AppCompatActivity() {

    private lateinit var postsListView: ListView
    private lateinit var writePostButton: Button
    private lateinit var postAdapter: PostAdapter
    private lateinit var postsList: MutableList<Post>
    private lateinit var currentUser: User

    companion object {
        private const val POST_ACTIVITY_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        postsListView = findViewById(R.id.postsListView)
        writePostButton = findViewById(R.id.writePostButton)

        // 게시글 목록 초기화
        postsList = mutableListOf(
            Post("", "제목1", "내용1", "작성자1", System.currentTimeMillis()),
            Post("", "제목2", "내용2", "작성자2", System.currentTimeMillis())
            // 원하는 만큼 게시글 추가
        )

        writePostButton.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivityForResult(intent, POST_ACTIVITY_REQUEST_CODE)
        }

        postsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedPost = postsList[position]
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("postTitle", selectedPost.title)
            intent.putExtra("postContent", selectedPost.content)
            startActivity(intent)
            // 선택한 게시물 보기 PostDetailActivity로 이동
        }

        val postAdapter = PostAdapter(this, postsList)
        postsListView.adapter = postAdapter

//        writePostButton.setOnClickListener {
//            val intent = Intent(this, PostActivity::class.java)
//            startActivity(intent)
//        }

        postsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedPost = postsList[position]
            val postRef = FirebaseDatabase.getInstance().getReference("posts/${selectedPost.postId}")
            postRef.removeValue() // Firebase Realtime Database에서 게시글 삭제
            postsList.removeAt(position)  // 게시글 목록에서도 삭제하여 갱신
            postAdapter.notifyDataSetChanged()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == POST_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val title = data?.getStringExtra("postTitle")
            val content = data?.getStringExtra("postContent")
            val author = currentUser.stuName // 현재 사용자의 이름을 가져옴
            val timestamp = System.currentTimeMillis()
//            val post = Post(postsList.size + 1, title, content, author, System.currentTimeMillis())
//            postsList.add(post)
//            postAdapter.notifyDataSetChanged() // 새로운 게시글을 추가하여 갱신
            // Firebase Realtime Database에 게시글 저장
            val database = FirebaseDatabase.getInstance()
            val postsRef = database.getReference("posts")
            val newPostRef = postsRef.push() // 새로운 게시글을 위한 참조 생성
            val newPost = Post(newPostRef.key!!, title!!, content!!, author, timestamp)
            newPostRef.setValue(newPost) // Firebase Realtime Database에 게시글 저장
            // 게시글 목록에 추가하여 갱신
            postsList.add(newPost)
            postAdapter.notifyDataSetChanged()

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