package cn.catherine.renderscriptdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import kotlinx.android.synthetic.main.aty_main.*

class MainAty : AppCompatActivity() {

    private val handler = Handler {
        val bitmap = it.obj as Bitmap
        layout_one.background = BitmapDrawable(bitmap)

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_main)
        Thread {
            val bitmap = getBlurBitmap()
            val mes = Message()
            mes.obj = bitmap
            handler.sendMessage(mes)

        }.start()
    }

    private fun getBlurBitmap(): Bitmap {
        var bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.icon)
        var renderScript = RenderScript.create(this)

        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)

        scriptIntrinsicBlur.setInput(input)

        scriptIntrinsicBlur.setRadius(25.0f)
        scriptIntrinsicBlur.forEach(output)
        output.copyTo(bitmap)
        renderScript.destroy()
        return bitmap
    }
}
