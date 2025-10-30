package com.example.pruebaandroid2

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebaandroid2.databinding.ActivityLoginBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var analytics: FirebaseAnalytics

    enum class ProviderType {
        BASIC
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Base_Theme_PruebaAndroid2)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        analytics = FirebaseAnalytics.getInstance(this)
        //Analytics Event
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)
        //Setup
        setup()
    }

    private fun setup() {
        title = "Login"
        binding.btnRegister.setOnClickListener {
            handleRegistration()
        }
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }
    private fun handleRegistration() {
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if (!validateInput(email, password)) return

        setLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                setLoading(false) // Hide progress bar
                if (task.isSuccessful) {
                    // Optional: Log analytics event for success
                    analytics.logEvent("register_success", null)
                    showHome(task.result?.user?.email ?: "", ProviderType.BASIC)
                } else {
                    // Show specific Firebase error
                    showAlert(task.exception?.message ?: "Error en el registro")
                }
            }
    }

    private fun handleLogin() {
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if (!validateInput(email, password)) return

        setLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                setLoading(false) // Hide progress bar
                if (task.isSuccessful) {
                    // Optional: Log analytics event for success
                    analytics.logEvent("login_success", null)
                    showHome(task.result?.user?.email ?: "", ProviderType.BASIC)
                } else {
                    // Show specific Firebase error
                    showAlert(task.exception?.message ?: "Email o contraseña incorrectos")
                }
            }
    }
    private fun validateInput(email: String, pass: String): Boolean {
        // Clear previous errors
        binding.editEmail.error = null
        binding.editPassword.error = null

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editEmail.error = "Por favor, introduce un email válido"
            return false
        }

        if (pass.isEmpty() || pass.length < 6) {
            binding.editPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        return true
    }
    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false
            binding.btnRegister.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true
            binding.btnRegister.isEnabled = true
        }
    }
    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error") // Recommend using R.string.error
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null) // Recommend R.string.accept
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
        finish()
    }
}