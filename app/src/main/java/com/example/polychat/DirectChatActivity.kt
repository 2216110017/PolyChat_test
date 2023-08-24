package com.example.polychat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class DirectChatActivity : AppCompatActivity() {

    private lateinit var chatListView: ListView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var attachButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_chat)

        chatListView = findViewById(R.id.chatListView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        attachButton = findViewById(R.id.attachButton)

        sendButton.setOnClickListener {
            // TODO: Firebase에 메시지 보내기
            messageEditText.text.clear()
        }

        attachButton.setOnClickListener {
            // TODO: 첨부
        }
    }
}