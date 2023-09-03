package com.example.polychat

import android.app.AlertDialog
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

        //뒤로버튼
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            showLogoutConfirmationDialog()
        }

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

    //뒤로버튼
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그아웃")
            .setMessage("로그아웃하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

}