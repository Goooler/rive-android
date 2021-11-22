package app.rive.runtime.kotlin

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.CallSuper
import app.rive.runtime.kotlin.renderers.RendererSwappy


abstract class RiveSurfaceView(context: Context, attrs: AttributeSet? = null) :
    SurfaceView(context, attrs),
    SurfaceHolder.Callback {
    private external fun cppInit(activity: Activity, initialSwapIntervalNS: Long)
    // TODO:    private external fun cppGetAverageFps(rendererAddress: Long): Float

    protected val activity by lazy(LazyThreadSafetyMode.NONE) {
        // If this fails we have a problem.
        this.getMaybeActivity()!!
    }

    protected abstract val renderer: RendererSwappy

    private val refreshPeriodNanos: Long by lazy {
        val msInNS: Long = 1000000
        val sInNS = 1000 * msInNS
        // Deprecated in API 30: keep this instead of having two separate paths.
        @Suppress("DEPRECATION")
        val refreshRateHz = activity.windowManager.defaultDisplay.refreshRate
        Log.i("RiveSurfaceHolder", String.format("Refresh rate: %.1f Hz", refreshRateHz))
        (sInNS / refreshRateHz).toLong()
    }

    private fun getMaybeActivity(): Activity? {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) {
                return ctx
            }
            ctx = ctx.baseContext
        }
        return null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Register this SurfaceView for the SurfaceHolder callbacks below
        holder.addCallback(this)
    }

    @CallSuper
    override fun surfaceCreated(holder: SurfaceHolder) {
        cppInit(activity, refreshPeriodNanos)
        renderer.setSurface(holder.surface)
        renderer.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        renderer.cleanup()
    }
}
