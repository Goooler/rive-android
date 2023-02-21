package app.rive.runtime.example.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.content.res.use
import app.rive.runtime.kotlin.RiveArtboardRenderer
import app.rive.runtime.kotlin.core.File

class RiveSwitch(context: Context, attrs: AttributeSet? = null) :

    AppCompatToggleButton(context, attrs) {

    private lateinit var riveArtboardRenderer: RiveArtboardRenderer
    private lateinit var onAnimation: String
    private lateinit var offAnimation: String
    private var stateMachineName: String? = null
    private lateinit var booleanStateInput: String

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            app.rive.runtime.example.R.styleable.RiveSwitch,
            0, 0
        ).use {
            val resourceId = it.getResourceId(
                app.rive.runtime.example.R.styleable.RiveSwitch_riveResource,
                -1
            )
            stateMachineName = it.getString(app.rive.runtime.example.R.styleable.RiveSwitch_riveStateMachine)
            onAnimation = it.getString(app.rive.runtime.example.R.styleable.RiveSwitch_riveOnAnimation) ?: "on"
            offAnimation = it.getString(app.rive.runtime.example.R.styleable.RiveSwitch_riveOffAnimation) ?: "off"
            booleanStateInput = it.getString(app.rive.runtime.example.R.styleable.RiveSwitch_riveBooleanStateInput) ?: "toggle"

            val resourceBytes = resources.openRawResource(resourceId).readBytes()
            val riveFile = File(resourceBytes)
            riveArtboardRenderer = RiveArtboardRenderer(autoplay = false)
            riveArtboardRenderer.setRiveFile(riveFile)
            stateMachineName?.let { name ->
                riveArtboardRenderer.setBooleanState(name, booleanStateInput, isChecked)
                riveArtboardRenderer.play(name, isStateMachine = true)
            }
        }
    }

    private fun setCheckedAnimation(checked: Boolean) {
        riveArtboardRenderer.let {
            it.stopAnimations()
            if (checked) {
                it.play(onAnimation)
            } else {
                it.play(offAnimation)
            }
        }
    }

    private fun setStateMachine(checked: Boolean) {
        stateMachineName?.let { stateMachine ->
            riveArtboardRenderer.setBooleanState(stateMachine, booleanStateInput, checked)
        }
    }

    override fun setChecked(checked: Boolean) {
        val output = super.setChecked(checked)

        if (stateMachineName == null) {
            setCheckedAnimation(checked)
        } else {
            setStateMachine(checked)
        }

        return output
    }


    override fun getTextOn(): CharSequence = super.getTextOn() ?: ""

    override fun getTextOff(): CharSequence = super.getTextOn() ?: ""
}