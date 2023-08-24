package com.example.polychat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Date

class GroupChatActivity : AppCompatActivity() {

    private lateinit var chatListView: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var attachButton: Button

    private val currentUser = FirebaseAuth.getInstance().currentUser // 현재 사용자 정보 가져오기



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        chatListView = findViewById(R.id.chatListView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        attachButton = findViewById(R.id.attachButton)

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                val message = ChatMessage(currentUser?.displayName ?: "Unknown", messageText, Date().time)
                sendMessageToFirebase(message)
                messageEditText.text.clear()
            }
        }

        attachButton.setOnClickListener {
            // TODO: 첨부
        }
    }
    private fun sendMessageToFirebase(message: ChatMessage) {
        val database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("group_chat")
        chatRef.push().setValue(message)
    }
}