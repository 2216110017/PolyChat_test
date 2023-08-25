package com.example.polychat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatListActivity : AppCompatActivity() {

    private lateinit var chatListView: ListView
    private lateinit var groupChatButton: Button
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        chatListView = findViewById(R.id.chatListView)
        groupChatButton = findViewById(R.id.groupChatButton)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            currentUser = intent.getParcelableExtra("currentUser",User::class.java)!!
        }

        // Firebase에서 채팅 목록 가져오기
        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        val chatList = mutableListOf<String>()

        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (chatSnapshot in dataSnapshot.children) {
                    val chat = chatSnapshot.getValue(String::class.java)
                    chat?.let { chatList.add(it) }
                }
                val adapter = ArrayAdapter(this@ChatListActivity, R.layout.list_item_chat, R.id.chatNameTextView, chatList)
                chatListView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
            }
        })


        groupChatButton.setOnClickListener {
            val intent = Intent(this, GroupChatActivity::class.java)
            startActivity(intent)
        }

        val adapter = ArrayAdapter(this, R.layout.list_item_chat, R.id.chatNameTextView, chatList)
        chatListView.adapter = adapter

        chatListView.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = chatList[position] // 목록에서 선택한 사용자의 정보를 가져옵니다
            val intent = Intent(this, DirectChatActivity::class.java)
            intent.putExtra("currentUser", currentUser)
            intent.putExtra("selectedUser", selectedUser)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
//                FirebaseAuth.getInstance().signOut()
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