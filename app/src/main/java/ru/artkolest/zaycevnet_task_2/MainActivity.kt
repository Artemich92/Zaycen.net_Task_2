package ru.artkolest.zaycevnet_task_2

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvPricesList: TextView
    private lateinit var etPrices: EditText
    private lateinit var btnAdd: Button
    private lateinit var etDiscount: EditText
    private lateinit var etOffset: EditText
    private lateinit var etReadLength: EditText
    private lateinit var btnApply: Button
    private lateinit var tvPriceAfterDiscount: TextView
    private val pricesList: ArrayList<Int> = ArrayList()
    private val pricesAfterDiscount: ArrayList<Int> = ArrayList()
    private var discount: Int = 1
    private var offset: Int? = 0
    private var readLength: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvPricesList = findViewById(R.id.tvPricesList)
        etPrices = findViewById(R.id.etPrices)
        btnAdd = findViewById(R.id.btnAdd)
        etDiscount = findViewById(R.id.etDiscount)
        etOffset = findViewById(R.id.etOffset)
        etReadLength = findViewById(R.id.etReadLength)
        btnApply = findViewById(R.id.btnApply)
        tvPriceAfterDiscount = findViewById(R.id.tvPriceAfterDiscount)

        btnAdd.setOnClickListener {
            if (!addPrices())
                btnAdd.isClickable.not()
        }

        btnApply.setOnClickListener {
            if (pricesList.isEmpty()) {
                btnApply.isClickable.not()
                return@setOnClickListener
            }
            if (!validationDiscount()) return@setOnClickListener
            if (!validationOffset()) return@setOnClickListener
            if (!validationReadLength()) return@setOnClickListener
            applyDiscount()
        }
    }

    private fun addPrices(): Boolean {
        return if (etPrices.text.toString().isNotEmpty()) {
            val price: Int = etPrices.text.toString().toInt()
            pricesList.add(price)
            tvPricesList.text = pricesList.joinToString()
            etPrices.text.clear()
            true
        } else {
            false
        }
    }

    private fun validationDiscount(): Boolean {
        paintTextInBlack(etDiscount)
        if (etDiscount.text.toString().isEmpty()) return false
        discount = etDiscount.text.toString().toInt()
        return when {
            discount > 99 -> {
                showToast("скидка не может быть больше 99%")
                paintTextInRed(etDiscount)
                false
            }
            discount < 1 -> {
                showToast("скидка не может быть меньше 1%")
                paintTextInRed(etDiscount)
                false
            }
            else -> true
        }
    }

    private fun validationOffset(): Boolean {
        paintTextInBlack(etOffset)
        if (etOffset.text.toString().isEmpty()) return false
        offset = etOffset.text.toString().toInt()
        var flag = false
        offset?.let {
            if (it > pricesList.size) {
                showToast("вы ввели всего ${pricesList.size} позиций товара")
                paintTextInRed(etOffset)
            }
            flag = true
        }
        return flag
    }

    private fun validationReadLength(): Boolean {
        paintTextInBlack(etReadLength)
        if (etReadLength.text.toString().isEmpty()) return false
        readLength = etReadLength.text.toString().toInt()
        val maxReadLength = pricesList.size - offset!!
        return if (maxReadLength < readLength) {
            showToast("слишком большое количество позиций, к которым нужно применить скидку")
            paintTextInRed(etReadLength)
            false
        } else {
            true
        }
    }

    private fun applyDiscount() {
        offset?.apply {
            val maxReadLength = offset!! + readLength
            for (i in offset!! until maxReadLength) {
                val newPrice = pricesList[i] * discount / 100
                pricesAfterDiscount.add(newPrice)
            }
        }
        tvPriceAfterDiscount.text = pricesAfterDiscount.joinToString()
        pricesAfterDiscount.clear()
    }

    private fun showToast(text: String){
        Toast.makeText(applicationContext,
            text, Toast.LENGTH_LONG).show()
    }

    private fun paintTextInRed(text: EditText){
        text.setTextColor(Color.RED)
    }

    private fun paintTextInBlack(text: EditText){
        text.setTextColor(Color.BLACK)
    }

    //скрываем клавиатуру при нажатии на любое место на экране, кроме текстовых полей
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                v.clearFocus()
                val imm: InputMethodManager =
                    applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
            }
        }
        return super.dispatchTouchEvent(event)
    }
}