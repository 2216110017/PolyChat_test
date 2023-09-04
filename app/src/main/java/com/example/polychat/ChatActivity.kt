package com.example.polychat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var userList: ArrayList<LoginActivity.LoginData>
    private lateinit var adapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //뒤로 버튼
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()  // 현재 액티비티를 종료하고 이전 액티비티로 돌아갑니다.
        }

        val userID = intent.getIntExtra("userID", -1)
        val listViewUsers = findViewById<ListView>(R.id.listViewUsers)
        val btnGroupChat = findViewById<Button>(R.id.btnGroupChat)

        userList = ArrayList()
        adapter = UserListAdapter(this, userList)

        listViewUsers.adapter = adapter

        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        // 사용자 목록을 가져와서 화면에 표시합니다.
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(LoginActivity.LoginData::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ChatActivity, "데이터 로드 실패: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // 사용자를 선택하면 해당 사용자와의 1:1 채팅 화면으로 이동합니다.
        listViewUsers.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = userList[position]
            val intent = Intent(this@ChatActivity, SingleChatActivity::class.java)
            intent.putExtra("userID", userID)
            intent.putExtra("selectedUser", selectedUser)
            startActivity(intent)
        }

        // 단체 채팅 화면으로 이동합니다.
        btnGroupChat.setOnClickListener {
            val intent = Intent(this@ChatActivity, GroupChatActivity::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
    }
}