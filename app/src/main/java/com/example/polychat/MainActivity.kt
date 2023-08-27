package com.example.polychat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // LoginActivity에서 전달받은 프로필 정보를 가져옵니다.
        val profile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("profile", LoginActivity.LoginData::class.java)
        } else {
            intent.getSerializableExtra("profile") as? LoginActivity.LoginData
        }


        // 사용자 프로필 정보 설정
        val tvProfileInfo = findViewById<TextView>(R.id.tvProfileInfo)
        profile?.let {
            tvProfileInfo.text = """
                이름: ${it.stuName}
                학번: ${it.stuNum}
                학과: ${it.department}
            """.trimIndent()
        }

        // 학과 게시판 버튼 클릭 리스너 설정
        val btnBoard = findViewById<Button>(R.id.btnBoard)
        btnBoard.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            intent.putExtra("userID", profile?.userID)
            startActivity(intent)
        }

        // 채팅 버튼 클릭 리스너 설정
        val btnChat = findViewById<Button>(R.id.btn_Chat)
        btnChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }
}

//class MainActivity : AppCompatActivity() {
//
//    private lateinit var stuNameTextView: TextView
//    private lateinit var stuNumTextView: TextView
//    private lateinit var departmentTextView: TextView
//    private lateinit var boardButton: Button
//    private lateinit var chatListButton: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        stuNameTextView = findViewById(R.id.stuNameTextView)
//        stuNumTextView = findViewById(R.id.stuNumTextView)
//        departmentTextView = findViewById(R.id.departmentTextView)
//        boardButton = findViewById(R.id.boardButton)
//        chatListButton = findViewById(R.id.chatListButton)
//
//        val stuNum = intent.getStringExtra("stuNum")
//        val stuName = intent.getStringExtra("stuName")
//        val department = intent.getStringExtra("department")
//        val userID = intent.getStringExtra("userID")
//
//        stuNameTextView.text = stuName
//        stuNumTextView.text = stuNum
//        departmentTextView.text = department
//
//        boardButton.setOnClickListener {
//            val intent = Intent(this, BoardActivity::class.java)
//            intent.putExtra("userID", userID)
//            intent.putExtra("stuName", stuName)
//            startActivity(intent)
//        }
//
//        chatListButton.setOnClickListener {
//            val intent = Intent(this, ChatListActivity::class.java)
//            intent.putExtra("userID", userID)
//            startActivity(intent)
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.logout -> {
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent) // 로그아웃 후 LopginActivity로 ㅇ ㅣ동
//                return true
//            }
//            R.id.settings -> {
//                val intent = Intent(this, SettingsActivity::class.java)
//                startActivity(intent)
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//}