package com.example.conexionfirebase
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.etEmail)
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val switchRegister = findViewById<Switch>(R.id.switchRegister)
        val googleSignInButton = findViewById<SignInButton>(R.id.btnGoogleSignIn)
        val contactListButton = findViewById<Button>(R.id.btnContactList)

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Ingresar o registrar usuario
        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            if (switchRegister.isChecked) {
                // Registrar nuevo usuario
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Cuenta creada exitosamente.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Autenticar usuario existente
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Ingreso exitoso.", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Google Sign-In
        googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)
        }

        // Ir a la lista de contactos
        contactListButton.setOnClickListener {
            startActivity(Intent(this, ContactListActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(this, "Google Sign-In exitoso.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
