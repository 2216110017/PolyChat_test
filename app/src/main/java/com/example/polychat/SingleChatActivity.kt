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

class SingleChatActivity : AppCompatActivity() {

    // 1:1 채팅에서 사용되는 메시지 데이터 클래스
    data class Message(
        val senderID: Int,
        val receiverID: Int,
        val messageText: String,
        val timestamp: Long
    )

    // Firebase 데이터베이스 관련 변수 선언
    private lateinit var database: DatabaseReference
    private lateinit var messagesAdapter: ArrayAdapter<String>
    private lateinit var messagesList: ArrayList<String>
//    private lateinit var loggedInUser: LoginActivity.LoginData
    private lateinit var chatPartner: LoginActivity.LoginData  // 채팅 상대 정보

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_chat)

        // 뒤로 가기 버튼 설정
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val userID = intent.getIntExtra("userID", -1)

        // 로그인한 사용자와 채팅 상대 정보 가져오기
//        loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            intent.getSerializableExtra("profile", LoginActivity.LoginData::class.java)
//        } else {
//            intent.getSerializableExtra("profile") as? LoginActivity.LoginData
//        }!!

        chatPartner = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("chatPartner", LoginActivity.LoginData::class.java)
        } else {
            intent.getSerializableExtra("chatPartner") as? LoginActivity.LoginData
        }!!

        // Firebase 데이터베이스 초기화
        val database = FirebaseDatabase.getInstance()
        val messageRef = database.getReference("users").child(userID.toString()).child("messages").child("singlechat")
//        val messageRef = database.getReference("users").child(loggedInUser.userID.toString()).child("messages").child("singlechat")

        // 메시지 리스트와 어댑터 초기화
        messagesList = ArrayList()
        messagesAdapter = ArrayAdapter(this, R.layout.list_item_received_message, messagesList)
        val listViewMessages = findViewById<ListView>(R.id.listViewMessages)
        listViewMessages.adapter = messagesAdapter

        // 두 사용자 간의 고유한 채팅 ID 생성
//        val chatID = if (loggedInUser.userID < chatPartner.userID) {
//            "${loggedInUser.userID}_${chatPartner.userID}"
//        } else {
//            "${chatPartner.userID}_${loggedInUser.userID}"
//        }
        val chatID = if (userID < chatPartner.userID) {
            "${userID}_${chatPartner.userID}"
        } else {
            "${chatPartner.userID}_${userID}"
        }

        // Firebase에서 해당 채팅 ID에 대한 메시지를 실시간으로 가져와 ListView에 표시
        messageRef.child(chatID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messagesList.clear()
                for (messageSnapshot in dataSnapshot.children) {
                    val message = messageSnapshot.child("messageText").getValue(String::class.java)
                    messagesList.add(message ?: "")
                }
                messagesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
                Toast.makeText(this@SingleChatActivity, "오류: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // "보내기" 버튼에 클릭 리스너 설정
        val btnSend = findViewById<Button>(R.id.btnSend)
        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)

        btnSend.setOnClickListener {
            val messageText = editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // 메시지 객체 생성, Firebase에 저장
                val messageMap = HashMap<String, Any>()
                messageMap["text"] = messageText
                messageMap["timestamp"] = System.currentTimeMillis()
                messageMap["senderID"] = userID
//                messageMap["senderID"] = loggedInUser.userID
                messageMap["receiverID"] = chatPartner.userID

                messageRef.child(chatID).push().setValue(messageMap)
                editTextMessage.text.clear()
            }
        }
    }
}