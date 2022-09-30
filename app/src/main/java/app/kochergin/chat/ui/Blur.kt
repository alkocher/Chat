package app.kochergin.chat.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.withTranslation

class Blur {

    private var renderScript: RenderScript? = null
    private var blurScript: ScriptIntrinsicBlur? = null

    fun onAttach(context: Context) {
        onDetached()
        renderScript = RenderScript.create(context)
        blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    }

    fun onDetached() {
        blurScript?.destroy()
        blurScript = null
        renderScript?.destroy()
        renderScript = null
    }

    fun getBlurredBackgroundOf(
        view: View,
        rectOnScreen: Rect,
        blurRadius: Float,
        @ColorInt overlayColor: Int = Color.TRANSPARENT,
        downscaleFactor: Int = DEFAULT_DOWNSCALE_FACTOR
    ): Bitmap {
        val bitmapToBlur = getViewBitmap(view)
        if (blurScript == null) {
            onAttach(view.context)
        }
        blurScript?.setRadius(blurRadius)
        val blurInput = Allocation.createFromBitmap(
            renderScript,
            bitmapToBlur,
            Allocation.MipmapControl.MIPMAP_NONE,
            Allocation.USAGE_SCRIPT
        )
        val blurOutput = Allocation.createTyped(renderScript, blurInput.type)

        blurInput.copyFrom(bitmapToBlur)
        blurScript?.setInput(blurInput)
        blurScript?.forEach(blurOutput)

        val blurredBitmap = Bitmap.createBitmap(
            bitmapToBlur.width,
            bitmapToBlur.height,
            Bitmap.Config.ARGB_8888
        )
        blurOutput.copyTo(blurredBitmap)

        bitmapToBlur.recycle()
        val scaledBlurredBitmap = Bitmap.createBitmap(
            rectOnScreen.width(),
            rectOnScreen.height(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(scaledBlurredBitmap)
        val viewLocations = IntArray(2)
        view.getLocationOnScreen(viewLocations)
        canvas.withTranslation(
            x = (viewLocations[0] - rectOnScreen.left).toFloat(),
            y = (viewLocations[1] - rectOnScreen.top).toFloat()
        ) {
            scale(downscaleFactor.toFloat(), downscaleFactor.toFloat())
            drawBitmap(blurredBitmap, 0F, 0F, null)
        }
        if (overlayColor != Color.TRANSPARENT) {
            canvas.drawColor(overlayColor)
        }
        blurredBitmap.recycle()
        return scaledBlurredBitmap
    }

    private fun getViewBitmap(view: View, downscaleFactor: Int = DEFAULT_DOWNSCALE_FACTOR): Bitmap {
        val viewWidth = view.measuredWidth
        val viewHeight = view.measuredHeight
        var scaledWidth = viewWidth / downscaleFactor
        var scaledHeight = viewHeight / downscaleFactor

        // нужно для того, чтобы избежать артефактов по краям при блюре
        scaledWidth = scaledWidth - scaledWidth % DEFAULT_EDGE_FIX_INT + DEFAULT_EDGE_FIX_INT
        scaledHeight = scaledHeight - scaledHeight % DEFAULT_EDGE_FIX_INT + DEFAULT_EDGE_FIX_INT

        val bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.scale(1F / downscaleFactor, 1F / downscaleFactor)
        if (view.background != null && view.background is ColorDrawable) {
            bitmap.eraseColor((view.background as ColorDrawable).color)
        } else {
            bitmap.eraseColor(Color.TRANSPARENT)
        }
        view.draw(canvas)
        return bitmap
    }

    companion object {
        const val DEFAULT_DOWNSCALE_FACTOR = 8
        private const val DEFAULT_EDGE_FIX_INT = 4
    }
}