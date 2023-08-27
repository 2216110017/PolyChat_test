package com.example.polychat

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import java.io.InputStreamReader
import java.io.Serializable

class LoginActivity : AppCompatActivity() {

    data class LoginData(
        val userID: Int,
        val stuNum: String,
        val stuName: String,
        val department: String,
        val email: String,
        val phone: String
    ) : Serializable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 팝업 메시지 표시
        AlertDialog.Builder(this)
            .setTitle("알림")
            .setMessage("이 앱은 졸업작품용으로 제작된 한국폴리텍대학 부산캠퍼스 채팅앱입니다." +
                "개인정보를 포함하는 기능(로그인, 채팅, 게시판)이 있기 때문에 거절시 앱이 종료됩니다." +
                "사용하는 개인정보 : 이름, 학번, 학과, 이메일, 전화번호")
            .setPositiveButton("확인") { _, _ -> } // 팝업닫기
            .setNegativeButton("거절") { _, _ -> finish()}  //앱종료
            .create()

        val editTextStuNum = findViewById<EditText>(R.id.editTextStuNum)
        val editTextStuName = findViewById<EditText>(R.id.editTextStuName)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val stuNum = editTextStuNum.text.toString()
            val stuName = editTextStuName.text.toString()
            val loginDataList = loadLoginData()
            val matchedData = loginDataList.find { it.stuNum == stuNum && it.stuName == stuName }

            if (matchedData != null) {
                // 로그인 성공
                // Firebase Realtime Database에 사용자 정보 저장
                val database = FirebaseDatabase.getInstance()
                val usersRef = database.getReference("users")
                usersRef.child(matchedData.userID.toString()).setValue(matchedData)

                // 2번 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("profile", matchedData) // 프로필 정보를 MainActivity으로 전달
                startActivity(intent)
                finish()
            } else {
                // 로그인 실패, 오류 메시지 표시
                Toast.makeText(this, "로그인 정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun loadLoginData(): List<LoginData> {
        val inputStream = assets.open("LoginDB.json")
        val jsonString = InputStreamReader(inputStream).readText()
        val type = object : TypeToken<List<LoginData>>() {}.type
        return Gson().fromJson(jsonString, type)
    }
}




//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var stuNumEditText: EditText
//    private lateinit var stuNameEditText: EditText
//    private lateinit var loginButton: Button
//
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        stuNumEditText = findViewById(R.id.stuNumEditText)
//        stuNameEditText = findViewById(R.id.stuNameEditText)
//        loginButton = findViewById(R.id.loginButton)
//
//
//        showPopupMessage()
//
//        loginButton.setOnClickListener {
//            val stuNum = stuNumEditText.text.toString()
//            val stuName = stuNameEditText.text.toString()
//
//
//            if (isValidLogin(stuNum, stuName)) {
//                val user = getUsersFromJson().firstOrNull { it.stuNum == stuNum && it.stuName == stuName }
//                user?.let {
//                    saveUserInfoToFirebase(it.userID, it.stuNum, it.stuName, it.department, it.email, it.phone) // 로그인 성공 시 사용자 정보 Firebase에 저장
//                    val intent = Intent(this, MainActivity::class.java)
//                    intent.putExtra("userID", it.userID)
//                    intent.putExtra("stuNum", it.stuNum)
//                    intent.putExtra("stuName", it.stuName)
//                    intent.putExtra("department", it.department)
//
//                    startActivity(intent)
//                    finish()
//                } ?: showError("로그인 자격 증명이 실패")
//            }
//        }
//    }
//    // LoginActivity.kt에서 로그인 성공 후 Firebase Realtime Database에 사용자 정보 저장
//    private fun saveUserInfoToFirebase(userID: String, stuNum: String, stuName: String, department: String, email: String, phone: String) {
//        val database = FirebaseDatabase.getInstance()
//        val usersRef = database.getReference("users") // "users" 노드에 사용자 정보 저장
//
//        // 사용자 정보를 Map 형태로 생성
//        val userMap = mapOf(
//            "userID" to userID,
//            "stuNum" to stuNum,
//            "stuName" to stuName,
//            "department" to department,
//            "email" to email,
//            "phone" to phone
//        )
//
//        // 사용자 정보를 Firebase Realtime Database에 저장
//        usersRef.child(userID.toString()).setValue(userMap)
//            .addOnSuccessListener {
//                showToast("사용자 정보가 저장되었습니다.")
//                // 저장 성공 시 처리
//            }
//            .addOnFailureListener {
//                showToast("사용자 정보 저장에 실패했습니다.")
//                // 저장 실패 시 처리
//            }
//
//    }
//    private fun showPopupMessage() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Notice")
//        builder.setMessage("이 앱은 졸업작품용으로 제작된 한국폴리텍대학 부산캠퍼스 채팅앱입니다." +
//                "개인정보를 포함하는 기능(로그인, 채팅, 게시판)이 있기 때문에 거절시 앱이 종료됩니다." +
//                "사용하는 개인정보 : 이름, 학번, 학과, 이메일, 전화번호" +
//                "사용하는 권한 : 파일에 접근(채팅 내 파일 첨부용)")
//        builder.setPositiveButton("수락") { _, _ -> }
//        builder.setNegativeButton("거절") { _, _ -> finish() }
//        builder.show()
//    }
//
//    private fun isValidLogin(stuNum: String, stuName: String): Boolean {
//        val users = getUsersFromJson()
//        for (user in users) {
//            if (user.stuNum == stuNum && user.stuName == stuName) {
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun getUsersFromJson(): List<User> {
//        val userList = mutableListOf<User>()
//        try {
//            val json = assets.open("LoginDB.json").bufferedReader(Charset.defaultCharset()).use { it.readText() }
//            val jsonArray = JSONArray(json)
//            for (i in 0 until jsonArray.length()) {
//                val jsonObject = jsonArray.getJSONObject(i)
//                val user = User(
//                    jsonObject.getString("userID"),
//                    jsonObject.getString("stuNum"),
//                    jsonObject.getString("stuName"),
//                    jsonObject.getString("department"),
//                    jsonObject.getString("email"),
//                    jsonObject.getString("phone")
//                )
//                userList.add(user)
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(this, "LoginDB.json의 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
//        }
//        return userList
//    }
//
//    private fun showError(message: String) {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Error")
//        builder.setMessage(message)
//        builder.setPositiveButton("OK") { _, _ -> }
//        builder.show()
//    }
//}