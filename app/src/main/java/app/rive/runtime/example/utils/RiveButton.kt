package app.rive.runtime.example.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.res.use
import app.rive.runtime.kotlin.RiveAnimationView

class RiveButton(context: Context, attrs: AttributeSet? = null) :
    RiveAnimationView(context, attrs) {

    private val pressAnimation: String?
    override val defaultAutoplay = true

    init {
        pressAnimation = context.theme.obtainStyledAttributes(
            attrs,
            app.rive.runtime.example.R.styleable.RiveButton,
            0, 0
        ).use {
            it.getString(app.rive.runtime.example.R.styleable.RiveButton_rivePressAnimation)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Pass through for performing click
        when (event?.action) {
            MotionEvent.ACTION_UP -> performClick()
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        pressAnimation?.let {
            renderer.stopAnimations()
            renderer.play(it)
            return true
        } ?: run {
            renderer.stopAnimations()
            renderer.play()
        }
        return super.performClick()
    }
}