package com.example.polychat

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONArray
import java.io.IOException
import java.nio.charset.Charset

class LoginActivity : AppCompatActivity() {

    private lateinit var stuNumEditText: EditText
    private lateinit var stuNameEditText: EditText
    private lateinit var loginButton: Button

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        stuNumEditText = findViewById(R.id.stuNumEditText)
        stuNameEditText = findViewById(R.id.stuNameEditText)
        loginButton = findViewById(R.id.loginButton)


        showPopupMessage()

        loginButton.setOnClickListener {
            val stuNum = stuNumEditText.text.toString()
            val stuName = stuNameEditText.text.toString()


            if (isValidLogin(stuNum, stuName)) {
                val user = getUsersFromJson().firstOrNull { it.stuNum == stuNum && it.stuName == stuName }
                user?.let {
                    saveUserInfoToFirebase(it.userID, it.stuNum, it.stuName, it.department, it.email, it.phone) // 로그인 성공 시 사용자 정보 Firebase에 저장
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userID", it.userID)
                    intent.putExtra("stuNum", it.stuNum)
                    intent.putExtra("stuName", it.stuName)
                    intent.putExtra("department", it.department)

                    startActivity(intent)
                    finish()
                } ?: showError("로그인 자격 증명이 실패")
            }
        }
    }
    // LoginActivity.kt에서 로그인 성공 후 Firebase Realtime Database에 사용자 정보 저장
    private fun saveUserInfoToFirebase(userID: String, stuNum: String, stuName: String, department: String, email: String, phone: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users") // "users" 노드에 사용자 정보 저장

        // 사용자 정보를 Map 형태로 생성
        val userMap = mapOf(
            "userID" to userID,
            "stuNum" to stuNum,
            "stuName" to stuName,
            "department" to department,
            "email" to email,
            "phone" to phone
        )

        // 사용자 정보를 Firebase Realtime Database에 저장
        usersRef.child(userID.toString()).setValue(userMap)
            .addOnSuccessListener {
                showToast("사용자 정보가 저장되었습니다.")
                // 저장 성공 시 처리
            }
            .addOnFailureListener {
                showToast("사용자 정보 저장에 실패했습니다.")
                // 저장 실패 시 처리
            }
    }
    private fun showPopupMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Notice")
        builder.setMessage("이 앱은 졸업작품용으로 제작된 한국폴리텍대학 부산캠퍼스 채팅앱입니다." +
                "개인정보를 포함하는 기능(로그인, 채팅, 게시판)이 있기 때문에 거절시 앱이 종료됩니다." +
                "사용하는 개인정보 : 이름, 학번, 학과, 이메일, 전화번호" +
                "사용하는 권한 : 파일에 접근(채팅 내 파일 첨부용)")
        builder.setPositiveButton("수락") { _, _ -> }
        builder.setNegativeButton("거절") { _, _ -> finish() }
        builder.show()
    }

    private fun isValidLogin(stuNum: String, stuName: String): Boolean {
        val users = getUsersFromJson()
        for (user in users) {
            if (user.stuNum == stuNum && user.stuName == stuName) {
                return true
            }
        }
        return false
    }

    private fun getUsersFromJson(): List<User> {
        val userList = mutableListOf<User>()
        try {
            val json = assets.open("LoginDB.json").bufferedReader(Charset.defaultCharset()).use { it.readText() }
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val user = User(
                    jsonObject.getString("userID"),
                    jsonObject.getString("stuNum"),
                    jsonObject.getString("stuName"),
                    jsonObject.getString("department"),
                    jsonObject.getString("email"),
                    jsonObject.getString("phone")
                )
                userList.add(user)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return userList
    }

    private fun showError(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { _, _ -> }
        builder.show()
    }
}