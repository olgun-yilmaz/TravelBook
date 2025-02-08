package com.olgunyilmaz.travelbook

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.olgunyilmaz.travelbook.databinding.ActivityMapsBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMapsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.travel_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_travel_item){
            val intent = Intent(this@MainActivity,MapsActivity :: class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}