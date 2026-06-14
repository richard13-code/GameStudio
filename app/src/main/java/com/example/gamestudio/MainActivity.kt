package com.example.gamestudio

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.example.gamestudio.core.FragmentCommunicator
import com.example.gamestudio.databinding.ActivityHomeBinding
import com.example.gamestudio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FragmentCommunicator {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun manageLoader(isVisible: Boolean) {
        binding.loaderView.isVisible = isVisible
        // Si tienes un loader en activity_home.xml, ponlo aquí
        // binding.loaderView.isVisible = isVisible
    }

    override fun manageBottomNavigation(isVisible: Boolean) {
    }
}