package com.ozgurerdogan.kotlin_travelbook.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.ozgurerdogan.kotlin_travelbook.Adapter.PlaceAdapter
import com.ozgurerdogan.kotlin_travelbook.Database.AppDatabase
import com.ozgurerdogan.kotlin_travelbook.Database.Place
import com.ozgurerdogan.kotlin_travelbook.Database.PlaceDao

import com.ozgurerdogan.kotlin_travelbook.R
import com.ozgurerdogan.kotlin_travelbook.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {

    private lateinit var binding:ActivityListBinding


    private lateinit var db: AppDatabase
    private lateinit var placeDao: PlaceDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db=Room.databaseBuilder(applicationContext,AppDatabase::class.java,"Places")
            .allowMainThreadQueries()
            .build()

        placeDao=db.placeDao()

        getData()

    }

    fun getData(){

        val placeList:List<Place> =placeDao.getAll()

        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.adapter=PlaceAdapter(placeList)



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_place,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.add_place){
            val intent= Intent(applicationContext,MapsActivity::class.java)
            intent.putExtra("info","new")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}