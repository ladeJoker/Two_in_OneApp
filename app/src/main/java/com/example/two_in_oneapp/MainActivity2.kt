package com.example.two_in_oneapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class MainActivity2 : AppCompatActivity() {
    lateinit var userGuess: ArrayList<Int>
    lateinit var leftedGuess: TextView
    lateinit var input: EditText
    lateinit var checkGuess: Button
    lateinit var myRV: RecyclerView
    lateinit var appAlert: ConstraintLayout
    var counter: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        userGuess = ArrayList()
        leftedGuess = findViewById(R.id.tvTrails)
        input = findViewById(R.id.etInput)
        checkGuess = findViewById(R.id.btCheck)


        myRV = findViewById(R.id.rvMain)
        appAlert = findViewById(R.id.alert)
        checkGuess.setOnClickListener (){ userEntry() }
    }
    fun perform(randomNum: Int , toCheck: Int){
        if (toCheck == randomNum) {
            leftedGuess.text = "Correct!!"
            userGuess.add(toCheck)
            myRV.adapter = RecyclerViewAdapter(userGuess)
            myRV.layoutManager = LinearLayoutManager(this)
            input.setText("")
        } else {
            userGuess.add(toCheck)
            myRV.adapter = RecyclerViewAdapter(userGuess)
            myRV.layoutManager = LinearLayoutManager(this)
            input.setText("")
            counter--
            leftedGuess.text = "You Have $counter guesses left"
            if(counter==3){
                leftedGuess.text = "Game Over!"
            }
        }
    }

    fun userEntry(){
        var randomNum = 1
        var isCheck = true

        if (counter > 0) {
            var toCheck = input.text.toString()
            try {
                Integer.parseInt(toCheck)
            }catch (e: Exception){
                isCheck = false
            }

            if (isCheck == true) {
                perform(randomNum , toCheck.toInt())
            } else {
                Snackbar.make(appAlert, "Enter a Number", Snackbar.LENGTH_LONG).show()
            }
        }else{
            Snackbar.make(appAlert, "Game Over :(", Snackbar.LENGTH_LONG).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem: MenuItem = menu!!.getItem(0)
        menuItem.setVisible(false)
        return super.onPrepareOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.guessNumber -> {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
                return true
            }
            R.id.phraseGuess -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.mainMenu -> {
                val intent = Intent(this, MainActivity3::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}