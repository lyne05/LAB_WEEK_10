package com.example.lab_week_10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val db by lazy { prepareDatabase() }

    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    private var lastDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeValueFromDatabase()
        prepareViewModel()
    }

    override fun onStart() {
        super.onStart()
        if (lastDate.isNotEmpty()) {
            Toast.makeText(this, "Last updated: $lastDate", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { total ->
            updateText(total)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total-database"
        ).allowMainThreadQueries().build()
    }

    private fun initializeValueFromDatabase() {
        val totalList = db.totalDao().getTotal(ID)

        if (totalList.isEmpty()) {
            val initialObject = TotalObject(0, Date().toString())
            db.totalDao().insert(Total(id = 1, total = initialObject))
            lastDate = initialObject.date
        } else {
            val data = totalList.first()
            lastDate = data.total.date
            viewModel.setTotal(data.total.value)
        }
    }

    override fun onPause() {
        super.onPause()

        val newValue = viewModel.total.value!!
        val newDate = Date().toString()

        db.totalDao().update(
            Total(
                id = ID,
                total = TotalObject(
                    value = newValue,
                    date = newDate
                )
            )
        )

        lastDate = newDate
    }

    companion object {
        const val ID: Long = 1
    }
}
