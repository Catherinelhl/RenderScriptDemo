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
        println("${it.arg1}:当前耗时：${System.currentTimeMillis() - currentTime}")
        false
    }

    var count: Int = 0
    private var currentTime: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_main)
        tv_dot.setOnClickListener {
            currentTime = System.currentTimeMillis()
            Thread {
                val bitmap = when (count % 2) {
                    0 -> getBlurBitmap(false)
                    else -> getBlurBitmap(true)

                }
                val mes = Message()
                mes.obj = bitmap
                mes.arg1 = count
                handler.sendMessage(mes)
                count++

            }.start()
        }

    }

    private fun getBlurBitmap(isScale: Boolean): Bitmap {
        var bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.icon)
        var width = bitmap.width
        var height = bitmap.height
        if (isScale) {
            width /= 8
            height /= 8
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        }
        var renderScript = RenderScript.create(this)

        val scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)

        scriptIntrinsicBlur.setInput(input)

        scriptIntrinsicBlur.setRadius(if (isScale) 1.0f else 25.0f)
        scriptIntrinsicBlur.forEach(output)
        output.copyTo(bitmap)
        renderScript.destroy()
        return bitmap
    }

}
