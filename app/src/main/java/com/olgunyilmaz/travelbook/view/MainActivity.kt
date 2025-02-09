package com.olgunyilmaz.travelbook.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.olgunyilmaz.travelbook.R
import com.olgunyilmaz.travelbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
        private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.travel_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_travel_item){
            val intent = Intent(this@MainActivity, MapsActivity :: class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}