package com.example.pruebaandroid2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.Base_Theme_PruebaAndroid2)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message","Integracion de Firebase completa")
        analytics.logEvent("InitScreen",bundle)
    }

}
//ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//insets