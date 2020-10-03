package com.example.firebaseregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    companion object {
        var TAG = SignUpActivity::class.java.simpleName
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

        db = Firebase.firestore

        btn_signup.setOnClickListener {
            signUp()

        }
    }

    private fun signUp() {
        val name = et_name.text.toString().trim()
        val username = et_username.text.toString().trim()
        val email = et_email_et.text.toString().trim()
        val password = et_password.text.toString()
        if (name.isEmpty()){
            et_name.error = "Nama harus diisi"
            return
        }
        if (password.isEmpty() || password.length < 8){
            et_password.error = "Password harus lebih dari 8 karakter"
            return
        }
        if (email.isEmpty()){
            et_email_et.error = "Email harus diisi"
            return
        }
        if (username.isEmpty()){
            et_username.error = "Username harus diisi"
            return
        }
        btn_signup.visibility = View.INVISIBLE
        loadProgressBar.visibility = View.VISIBLE
        db.collection("users").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "addOnSuccessListener ${document.data["username"]}")
                    if (document.data["username"] == username) {
                        Log.d(TAG, "addOnSuccessListener ${document.data["username"]}")
                        et_username.error = "Username already exist"
                        Toast.makeText(this, "Username already exist", Toast.LENGTH_SHORT)
                            .show()
                        loadProgressBar.visibility = View.GONE
                        btn_signup.visibility = View.VISIBLE
                        return@addOnSuccessListener
                    }
                }
                register(email, password, name, username)
            }
            .addOnFailureListener {e ->
                btn_signup.visibility = View.VISIBLE
                loadProgressBar.visibility = View.GONE
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT ).show()
            }
    }

    private fun register(email: String, password: String, name: String, username: String ){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user?.updateProfile(profileUpdates)
                    val userdata = hashMapOf(
                        "name" to name,
                        "username" to username,
                        "email" to email
                    )
                    db.collection("users")
                        .add(userdata)
                    loadProgressBar.visibility = View.GONE
                    val intent =
                        Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    loadProgressBar.visibility = View.GONE
                    btn_signup.visibility = View.VISIBLE
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}
