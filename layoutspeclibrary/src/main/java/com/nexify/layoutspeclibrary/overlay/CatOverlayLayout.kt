import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.nexify.layoutspeclibrary.NexApplication
import com.nexify.layoutspeclibrary.data.NTLine
import com.nexify.layoutspeclibrary.data.NTLineMap

class CatOverlayLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var popupWindow: PopupWindow? = null
    private val borderPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }
    private var firstViewRect: Rect = Rect()
    // 点击后的详细信息
    private var dimensionText: String = ""
    private val borderRect = Rect()
    private val textPaint = Paint().apply {
        color = Color.BLUE
        textSize = 40f
        strokeWidth = 7f
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val dashPaint =  Paint().apply {
        color = Color.RED
        strokeWidth = 4f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f) // 设置虚线样式
        isAntiAlias = true
    }
    private var targetMap = emptyMap<String,Int>()
    private var leftMap = mutableMapOf<String,Float>()
    private var nTLineMap = NTLineMap(null,null,null,null)
    init {

        val popupView = TextView(context).apply {
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xAA000000.toInt())
            setTextColor(0xFFFFFFFF.toInt())
        }
        popupView.setOnClickListener {
            Toast.makeText(this.context,"NIHAO",Toast.LENGTH_SHORT).show()
        }
        popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow?.isOutsideTouchable = true

        setWillNotDraw(false)
        isClickable = false
    }
    private fun getViewDistanceToParent(view: View): Map<String, Int> {
        val childRect = Rect()
        val parentRect = Rect()

        // 获取当前视图的边界矩形
        view.getDrawingRect(childRect)

        // 获取当前视图的父视图，并获取父视图的矩形边界
        val parentView = view.parent as? View
        parentView?.getDrawingRect(parentRect)

        // 获取当前视图的屏幕坐标
        val childLocation = IntArray(2)
        view.getLocationOnScreen(childLocation)

        // 获取父视图的屏幕坐标
        val parentLocation = IntArray(2)
        parentView?.getLocationOnScreen(parentLocation)

        // 计算视图与父视图的边界位置
        val topDistance = childLocation[1] - parentLocation[1]
        val bottomDistance = (parentLocation[1] + parentView!!.height) - (childLocation[1] + view.height)
        val leftDistance = childLocation[0] - parentLocation[0]
        val rightDistance = (parentLocation[0] + parentView.width) - (childLocation[0] + view.width)

        // 返回各个方向的距离
        return mapOf(
            "top" to topDistance,
            "bottom" to bottomDistance,
            "left" to leftDistance,
            "right" to rightDistance
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!borderRect.isEmpty) {
            canvas.drawRect(borderRect, borderPaint)
            canvas.drawRect(firstViewRect, borderPaint)
            canvas.drawText(
                dimensionText,
                firstViewRect.left.toFloat(),
                firstViewRect.top.toFloat(),
                textPaint
            )
            canvas.drawText(targetMap["left"].toString(),(nTLineMap.left?.startX?:0f)- targetMap["left"]?.toFloat()?.div(2)!!,(nTLineMap.left?.stopY?:0f) -10f,textPaint)
            canvas.drawText(targetMap["right"].toString(),(nTLineMap.right?.startX?:0f) + targetMap["right"]?.toFloat()?.div(2)!!,(nTLineMap.right?.stopY?:0f) -10f,textPaint)
            canvas.drawText(targetMap["top"].toString(),(nTLineMap.top?.startX?:0f) + 15f ,(nTLineMap.top?.startY?:0f) - targetMap["top"]?.toFloat()?.div(2)!!,textPaint)
            canvas.drawText(targetMap["bottom"].toString(),(nTLineMap.bottom?.startX?:0f) + 15f ,(nTLineMap.bottom?.startY?:0f) + targetMap["bottom"]?.toFloat()?.div(2)!!,textPaint)
            canvas.drawLine(nTLineMap.left?.startX?:0f,nTLineMap.left?.startY?:0f,nTLineMap.left?.stopX?:0f,nTLineMap.left?.stopY?:0f,dashPaint)
            canvas.drawLine(nTLineMap.right?.startX?:0f,nTLineMap.right?.startY?:0f,nTLineMap.right?.stopX?:0f,nTLineMap.right?.stopY?:0f,dashPaint)
            canvas.drawLine(nTLineMap.top?.startX?:0f,nTLineMap.top?.startY?:0f,nTLineMap.top?.stopX?:0f,nTLineMap.top?.stopY?:0f,dashPaint)
            canvas.drawLine(nTLineMap.bottom?.startX?:0f,nTLineMap.bottom?.startY?:0f,nTLineMap.bottom?.stopX?:0f,nTLineMap.bottom?.stopY?:0f,dashPaint)

        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val clickX = event.rawX.toInt()
            val clickY = event.rawY.toInt()

            // 遍历父布局查找被点击的View
            val rootView = NexApplication.activityWeakReference?.get()?.window?.decorView
            val targetView = findViewAtPosition(rootView, clickX, clickY)

            if (targetView != null) {
                val rect = Rect()
                targetView.getGlobalVisibleRect(rect)
                targetMap = getViewDistanceToParent(targetView)
                val info =
                        "ID: ${targetView.resources.getResourceName(targetView.id)}\n" +
                        "Class: ${targetView::class.java.simpleName}\n" +
                        "Rect: $rect\n" +
                        "Width: ${targetView.width}\n" +
                        "Height: ${targetView.height}"
                // 显示PopupWindow
                val textView = popupWindow?.contentView as? TextView
                textView?.text = info
                highlightView(targetView,targetMap)
                popupWindow?.showAtLocation(this, android.view.Gravity.NO_GRAVITY, clickX, clickY)
                invalidate()
            }
        }
        return true
    }

    private fun highlightView(view: View,map:Map<String,Int>) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)

        val overlayLocation = IntArray(2)
        this.getLocationOnScreen(overlayLocation)

        borderRect.set(
            location[0] - overlayLocation[0],
            location[1] - overlayLocation[1],
            location[0] + view.width - overlayLocation[0],
            location[1] + view.height - overlayLocation[1]
        )
        nTLineMap.left = NTLine(location[0].toFloat(), location[1] - overlayLocation[1] + view.height / 2 + 0f,(location[0] - map["left"]!!).toFloat(),location[1] - overlayLocation[1] + view.height / 2 + 0f)
        nTLineMap.right = NTLine(location[0].toFloat() + view.width , location[1] - overlayLocation[1] + view.height / 2 + 0f,  (location[0] + view.width + map["right"]!!).toFloat(),location[1] - overlayLocation[1] + view.height / 2 + 0f)
        nTLineMap.top = NTLine(location[0] + view.width /2 - overlayLocation[0] + 0f, location[1].toFloat() - overlayLocation[1] , location[0] + view.width /2 - overlayLocation[0] + 0f,(location[1] - map["top"]!!).toFloat())
        nTLineMap.bottom = NTLine(location[0] + view.width /2 - overlayLocation[0] + 0f, location[1].toFloat() - overlayLocation[1] + view.height , location[0] + view.width /2 - overlayLocation[0] + 0f,(location[1] + map["bottom"]!! + view.height).toFloat())

        leftMap["startX"] = location[0].toFloat()
        leftMap["startY"] = location[1] - overlayLocation[1] + view.height / 2 + 0f
        leftMap["stopX"] = (location[0] - map["left"]!!).toFloat()
        leftMap["stopY"] = location[1] - overlayLocation[1] + view.height / 2 + 0f
        invalidate()
    }
    // 递归查找点击位置的View
    private fun findViewAtPosition(view: View?, x: Int, y: Int): View? {
        if (view == null || view !is ViewGroup) return null
        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i)
            val rect = Rect()
            child.getGlobalVisibleRect(rect)
            if (rect.contains(x, y)) {
                return if (child is ViewGroup) findViewAtPosition(child, x, y) ?: child else child
            }
        }
        return null
    }
}
