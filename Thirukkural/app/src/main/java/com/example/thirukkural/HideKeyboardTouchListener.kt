package com.example.thirukkural

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout

class HideKeyboardTouchListener(private val context: Context) : View.OnTouchListener {
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            view?.let {
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
        return false
    }
}
