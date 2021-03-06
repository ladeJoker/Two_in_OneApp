package com.example.two_in_oneapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences //to keep track of the higher score
    lateinit var myRV: RecyclerView
    lateinit var checkBut: Button
    lateinit var remainGuesses: TextView
    lateinit var sentenceToGuess: TextView
    lateinit var currentGuess: TextView
    lateinit var input: EditText
    lateinit var CorrectGuess: ArrayList<String>
    lateinit var lettersArray: ArrayList<Char>
    lateinit var allUserEntry: ArrayList<String>
    lateinit var appAlert: ConstraintLayout
    lateinit var encodedSentence: String
    var counterGuessLetter: Int = 10 //number of chances + stop condition for letter
    var counterGuessWords: Int = 10 //number of chances + stop condition for word
    var counterLetter: Int = 0
    private var highestScore : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        callData()

        //------------------------------------------------------------------initiate all variables
        checkBut = findViewById(R.id.btCheck) //Button
        remainGuesses = findViewById(R.id.tvRemain) //the position to display remaining guesses
        sentenceToGuess = findViewById(R.id.tvSentence) //the position of the game sentence
        currentGuess = findViewById(R.id.tvGuessedLetter) //to display the current user entry
        input = findViewById(R.id.etPlain) //to receive input from user
        lettersArray = arrayListOf() //to store all user correct letter answer
        appAlert = findViewById(R.id.taskAlert) //feedback alert
        myRV = findViewById(R.id.rvView) //for recycler view
        allUserEntry = arrayListOf() //to store all user entry either in letter/word game, in order to send it to the recycler view
        //-----------------------------------------insert the sentence you want to play(Hello world)
        currentGuess.setText("The Highest Score Is $highestScore")
        CorrectGuess = arrayListOf("Hello", "success")
        var forGuess = CorrectGuess[Random.nextInt(CorrectGuess.size)] //get a random sentense to guess
        encodedSentence = encodeAll(forGuess) //encode it all to stars
        sentenceToGuess.setText(encodedSentence) //display it to the user
        var trFal = listOf<Int>(1,2)
        var boolChoice = Random.nextInt(trFal.size) //to play randomly, either by letters or words
        if(boolChoice != 1){
            input.setHint("Guess a Letter")
            checkBut.setOnClickListener { letters( forGuess) }
        }else{
            input.setHint("Guess the Full Phrase")
            checkBut.setOnClickListener { words( forGuess) }
        }
    }

    fun toRecyclerView(entry: String) { //a string to display it to the user in the recycler view
        allUserEntry.add(entry) //Array list to store input
        myRV.adapter =
            RecyclerViewAdapter1(allUserEntry) //as the parameter accept only an array list
        myRV.layoutManager = LinearLayoutManager(this)
        if(allUserEntry.size!=0)
            myRV.smoothScrollToPosition(allUserEntry.size - 1)
    }

    fun words(sentence: String) {
        if (counterGuessWords >= 1) {//Stop Condition
            var userEntry = input.text.toString() //get input from user
            input.setText("") //clear the plain text entry space
            toRecyclerView(userEntry)

            if (userEntry != "" && userEntry != " " && !userEntry.isDigitsOnly() && userEntry.length != 1) { //validate the entry
                currentGuess.setText("Guessed Word: $userEntry") //display the input to the user
                if (userEntry.equals(sentence, true)) { //if there is a true guess
                    sentenceToGuess.setText(sentence) //show it to the user
                    currentGuess.setText("You Got It :)") //Feadback
                    counterGuessWords = 0 //as stop condition, game stop
                    highestScore
                    highestScore++
                    saveData()
                    playAgain(sentence)
                } else {
                    currentGuess.setText("LOL") //if the user enter something wrong
                    counterGuessWords-- //wrong guess leads to decrease the chances
                    remainGuesses.setText("You have $counterGuessWords phrase guesses left") //display to the user remaining guesses
                }
            } else {
                currentGuess.setText("LOL")
                Snackbar.make(appAlert, "Enter Correct Phrase", Snackbar.LENGTH_LONG).show()
                input.setText("")
            }
        } else {
            Snackbar.make(appAlert, "Game Over :)", Snackbar.LENGTH_LONG).show() //Feadback
            input.setText("")
        }
    }

    //______________________________________________________________________________________________
    //----------------------------------------Letter Functions--------------------------------------
    //______________________________________________________________________________________________
    fun encodeAll(sentence: String): String {
        var new = ""
        for (i in sentence) {// convert all the letters only to stars
            if (i == ' ') {
                new = new + ' '
            } else {
                new = new + '*'
            }
        }
        return new
    }

    fun deCodeLetter(someString: ArrayList<Char>, sentence: String, letter: Char): String {
        var check = false //to check if the guessed letter exist or not
        var fillMe = "" //to update the user games result
        /*
        Next: go over the true sentence and check each letter,
        if exist then --> replace position by the letter
        if nothing exist --> replace position by space
        if Unknown-Letter exist --> replace position by "*"
         */
        for (i in sentence) {
            for (k in someString) {
                if (i.toString().toLowerCase() == k.toString().toLowerCase()) {
                    check = true
                    if (k == letter) { //calculate the number of the current guess of the user
                        counterLetter++
                    }
                    break
                }
            }
            if (check == true) {
                fillMe = fillMe + i.toString()
                check = false
            } else if (i.toString() == " ") {
                fillMe = fillMe + " "
            } else {
                fillMe = fillMe + "*"
            }

        }
        return fillMe //return the game result until now
    }

    fun checkLetter(userGuess: Char, sentence: String): Boolean {
        //only check if the letter exist generally
        for (i in sentence) {
            if (userGuess.toLowerCase() == i.toLowerCase()) {
                return true
            }
        }
        return false
    }

    fun letters(sentence: String) {
        if (counterGuessLetter >= 1) {
            var userEntry = input.text.toString() //get input from user
            input.setText("") //clear the plain text
            if (userEntry != "" && userEntry != " " && !userEntry.isDigitsOnly() && userEntry.length == 1) { //validate the entry
                currentGuess.setText("Guessed Letter: $userEntry")
                if (checkLetter(userEntry[0], sentence) == true) { //if there is a true guess
                    highestScore++
                    lettersArray.add(userEntry[0])
                    encodedSentence = deCodeLetter( //update the encoded message with the correct guessed letters
                        lettersArray,
                        sentence,
                        userEntry[0]
                    )
                    toRecyclerView("Found $counterLetter of: $userEntry")//***************************************************
                    counterLetter = 0//clear
                    sentenceToGuess.setText(encodedSentence) //show it to the user
                    if (encodedSentence.toLowerCase() == sentence.toLowerCase()) {
                        currentGuess.setText("You Got It :)")
                        counterGuessLetter = 0 // game stop
                        saveData()
                        playAgain(sentence)
                    }
                    remainGuesses.setText("You have $counterGuessLetter letter guesses left")
                } else {
                    currentGuess.setText("LOL")
                    counterGuessLetter--//he guess correct
                    remainGuesses.setText("You have $counterGuessLetter letter guesses left")
                }
            } else {
                currentGuess.setText("LOL")
                Snackbar.make(appAlert, "Enter Correct Letter", Snackbar.LENGTH_LONG).show()
                input.setText("")
            }
        } else {
            Snackbar.make(appAlert, "Game Over :)", Snackbar.LENGTH_LONG).show()
            input.setText("")
        }
    }

    fun callData(){
        highestScore = sharedPreferences.getInt("HQ",0)!!
    }

    fun saveData(){
        with(sharedPreferences.edit()) {
            putInt("HQ",highestScore)
            apply()
        }
    }

    private fun playAgain(sentence: String){

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("The Correct Phrase was $sentence \nWould You Like To Play Again:")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ -> this.recreate() }
            .setNegativeButton("No"){dialog,_ -> dialog.cancel() }
        val alert=dialogBuilder.create()
        alert.setTitle("Game Over!!")
        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
        override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem: MenuItem = menu!!.getItem(1)
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