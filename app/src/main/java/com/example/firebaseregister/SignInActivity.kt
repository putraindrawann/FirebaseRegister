package com.example.firebaseregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    companion object {
        var TAG = SignInActivity::class.java.simpleName

    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        btn_signin.setOnClickListener {
            signIn()
        }

        btn_signup.setOnClickListener {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn() {
        btn_signin.visibility = View.INVISIBLE
        loadProgressBar.visibility = View.VISIBLE
        val email = et_email_et.text.toString().trim()
        val password = et_password.text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val name = user?.email
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    intent.putExtra("EXTRA_NAME", name)
                    loadProgressBar.visibility = View.GONE
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    loadProgressBar.visibility = View.GONE
                    btn_signin.visibility = View.VISIBLE
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
    }
}
