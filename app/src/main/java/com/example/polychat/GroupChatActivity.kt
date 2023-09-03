package com.example.polychat

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupChatActivity : AppCompatActivity() {

    // Firebase 데이터베이스 참조 변수 및 필요한 변수들 선언
    private lateinit var database: DatabaseReference
    private lateinit var messagesAdapter: ArrayAdapter<String>
    private lateinit var messagesList: ArrayList<String>
    private lateinit var loggedInUser: LoginActivity.LoginData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        //뒤로 버튼
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()  // 현재 액티비티를 종료하고 이전 액티비티로 돌아갑니다.
        }

        // Firebase 데이터베이스 초기화
        database = FirebaseDatabase.getInstance().getReference("users/group_chat")

        // 로그인한 사용자의 정보를 가져옵니다.
        loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("profile", LoginActivity.LoginData::class.java)
        } else {
            intent.getSerializableExtra("profile") as? LoginActivity.LoginData
        }!!

        // 메시지 리스트 및 어댑터 초기화
        messagesList = ArrayList()
        messagesAdapter = ArrayAdapter(this, R.layout.list_item_received_message, messagesList)
        val listViewMessages = findViewById<ListView>(R.id.listViewMessages)
        listViewMessages.adapter = messagesAdapter

        // Firebase 데이터베이스에서 메시지를 가져와 ListView에 표시합니다.
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messagesList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val message = postSnapshot.child("messageText").getValue(String::class.java)
                    messagesList.add(message ?: "")
                }
                messagesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
                Toast.makeText(this@GroupChatActivity, "오류: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // "보내기" 버튼에 클릭 리스너 설정
        val btnSend = findViewById<Button>(R.id.btnSend)
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)

        btnSend.setOnClickListener {
            val messageText = editTextMessage.text.toString()
            if (messageText.isNotEmpty()) {
                val message = Message(loggedInUser.stuName, messageText, System.currentTimeMillis())
                database.push().setValue(message)
                editTextMessage.text.clear()
            }
        }
    }
}

// 메시지를 나타내는 데이터 클래스
data class Message(
    val senderName: String,
    val messageText: String,
    val timestamp: Long
)