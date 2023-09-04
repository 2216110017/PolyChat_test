package com.example.polychat

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
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class GroupChatActivity : AppCompatActivity() {

    // Firebase 데이터베이스 관련 변수 선언
    private lateinit var database: FirebaseDatabase
    private lateinit var messageRef: DatabaseReference
    private lateinit var messagesAdapter: ArrayAdapter<String>
    private lateinit var messagesList: ArrayList<String>
//    private lateinit var loggedInUser: LoginActivity.LoginData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        // 뒤로 가기 버튼 설정
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()  // 현재 액티비티를 종료, 이전 액티비티로 이동
        }

        val userID = intent.getIntExtra("userID", -1)

//        // 로그인한 사용자의 정보 가져오기
//        loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            intent.getSerializableExtra("profile", LoginActivity.LoginData::class.java)
//        } else {
//            intent.getSerializableExtra("profile") as? LoginActivity.LoginData
//        }!!

        // Firebase 데이터베이스 초기화
        database = FirebaseDatabase.getInstance()
        messageRef = database.getReference("users").child(userID.toString()).child("messages").child("groupchat")
//        messageRef = database.getReference("users").child(loggedInUser.userID.toString()).child("messages").child("groupchat")
        // 메시지 리스트, 어댑터 초기화
        messagesList = ArrayList()
        messagesAdapter = ArrayAdapter(this, R.layout.list_item_received_message, messagesList)
        val listViewMessages = findViewById<ListView>(R.id.listViewMessages)
        listViewMessages.adapter = messagesAdapter

        // Firebase에서 메시지를 가져와 ListView에 표시
        messageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messagesList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val message = postSnapshot.child("text").getValue(String::class.java)
                    messagesList.add(message ?: "")
                }
                messagesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 발생 시
                Toast.makeText(this@GroupChatActivity, "오류: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // "보내기" 버튼에 클릭 리스너 설정
        val btnSend = findViewById<Button>(R.id.btnSend)
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)

        btnSend.setOnClickListener {
            val messageText = editTextMessage.text.toString()
            if (messageText.isNotEmpty()) {
                val message = HashMap<String, Any>()
                message["text"] = messageText
                message["timestamp"] = ServerValue.TIMESTAMP
//                message["senderID"] = loggedInUser.userID
                message["senderID"] = userID

                messageRef.push().setValue(message)
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
