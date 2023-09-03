package com.example.polychat

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
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

        // SharedPreferences 초기화
        val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val checkBoxRememberLogin = findViewById<CheckBox>(R.id.checkBoxRememberLogin)

        // 체크박스 상태를 SharedPreferences에서 불러오기
        val rememberLogin = sharedPreferences.getBoolean("rememberLogin", false)
        if (rememberLogin) {
            val savedStuNum = sharedPreferences.getString("stuNum", "")
            val savedStuName = sharedPreferences.getString("stuName", "")
            editTextStuNum.setText(savedStuNum)
            editTextStuName.setText(savedStuName)
            checkBoxRememberLogin.isChecked = true
        }

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

                if (checkBoxRememberLogin.isChecked) {
                    // '로그인 유지' 체크박스가 선택된 경우
                    sharedPreferences.edit().apply {
                        putBoolean("rememberLogin", true)
                        putString("stuNum", stuNum)
                        putString("stuName", stuName)
                        apply()
                    }
                } else {
                    // 체크박스가 선택되지 않은 경우
                    sharedPreferences.edit().apply {
                        putBoolean("rememberLogin", false)
                        remove("stuNum")
                        remove("stuName")
                        apply()
                    }
                }

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

