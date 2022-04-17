package inu.withus.cameraintegration.textdetector

import android.content.Intent
import android.content.Intent.*
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import inu.withus.restructversion.GraphicOverlay.Graphic
import com.google.mlkit.vision.text.Text
import inu.withus.restructversion.DetectorActivity
import inu.withus.restructversion.GraphicOverlay
import inu.withus.restructversion.RegisterFoodActivity
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class TextGraphic
constructor(
    overlay: GraphicOverlay?,
    private val text: Text,
    private val shouldGroupTextInBlocks: Boolean,
    private val showLanguageTag: Boolean,
    private var expireDateCount: HashMap<String, Int>,
    private var foodName: HashMap<String, Float>,
    private val start: Long
) : Graphic(overlay) {

    private val rectPaint: Paint = Paint()
    private val textPaint: Paint
    private val labelPaint: Paint
    private var maxCount: String? = ""
    private var maxHeight: String? = ""
    private var info: String = ""
    private val datePattern =
        Regex("^([0-9]*(.|,|-|년))*\\s*(0[1-9]|1[012])(.|,|-|월)\\s*(0[1-9]|[12][0-9]|3[01])(.|,|-|일)?(\\S[가-힣0-9a-zA-Z])?$")
    private val namePattern = Regex("^[가-힣]*$")
    val intent = Intent(applicationContext, RegisterFoodActivity::class.java)

    init {
        rectPaint.color = MARKER_COLOR
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = STROKE_WIDTH
        textPaint = Paint()
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
        labelPaint = Paint()
        labelPaint.color = MARKER_COLOR
        labelPaint.style = Paint.Style.FILL
        // Redraw the overlay, as this graphic has been added.
        postInvalidate()
    }

    /** Draws the text block annotations for position, size, and raw value on the supplied canvas. */
    override fun draw(canvas: Canvas?) {
        Log.d(TAG, "Start")
        Log.d(TAG, "Text is: " + text.text)

        for (textBlock in text.textBlocks) { // Renders the text at the bottom of the box.

            Log.d(TAG, "current time1 : " + System.currentTimeMillis())
            Log.d(TAG, "Textblock text is: " + textBlock.text)
            Log.d(TAG, "TextBlock boundingbox is : " + textBlock.boundingBox)

            if (datePattern.matches(textBlock.text)) {
                try {
                    Log.d(TAG, "text[0] : " + textBlock.text[0])
                    if (textBlock.text[0] == '2') {
                        // 유통기한이 2022와 같이 2로 시작할 경우
                        info = textBlock.text.substring(0, textBlock.text.indexOf("까"))
                        Log.d(TAG, "if문")
                    } else {
                        // 유통기한 앞에 문자가 왔을 경우
                        info = textBlock.text.substring(6, textBlock.text.length)
                        Log.d(TAG, "else문")
                    }
                    Log.d(TAG, "info2 : " + info)

                    if (expireDateCount.containsKey(info)) {
                        expireDateCount[info] =
                            (expireDateCount[info] ?: 0) + 1
                    } else {
                        expireDateCount[info] = 1
                    }

                } catch (e: StringIndexOutOfBoundsException) {
                    var info = textBlock.text
                    Log.d(TAG, "info3 : " + info)
                    if (expireDateCount.containsKey(info)) {
                        expireDateCount[info] =
                            (expireDateCount[info] ?: 0) + 1
                    } else {
                        expireDateCount[info] = 1
                    }
                }
            } else if (namePattern.matches(textBlock.text)){
                val textHeight = drawText(
                    getFormattedText(textBlock.text, textBlock.recognizedLanguage),
                    RectF(textBlock.boundingBox),
                    TEXT_SIZE * textBlock.lines.size + 2 * STROKE_WIDTH,
                    canvas!!
                )
                Log.d(TAG, "textHeight : $textHeight")
                if (!(foodName.containsValue(textHeight))) {
                    foodName[textBlock.text] = textHeight
                    Log.d(TAG, "foodName text : " + textBlock.text)
                    Log.d(TAG, "textBlock lines size : " + textBlock.lines.size)
                }
            }

            Log.d(TAG, "diff : " + System.currentTimeMillis().minus(start))
            if ((System.currentTimeMillis() - start > 10000) && (maxCount == null)) {
                val objectIntent =
                    Intent(applicationContext, DetectorActivity::class.java)
                objectIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                applicationContext.startActivity(
                    objectIntent.addFlags(
                        FLAG_ACTIVITY_NEW_TASK
                    )
                )
            } else {
                maxCount = expireDateCount.maxByOrNull { it.value }?.key
                intent.putExtra("expireDate", maxCount)
                Log.d(TAG, "hashMap1 : " + expireDateCount.toString())
                maxHeight = foodName.maxByOrNull { it.value }?.key
                intent.putExtra("foodName", maxHeight)
                Log.d(TAG, "hashMap2 : " + foodName.toString())
                Log.d(TAG, "maxCount1 : $maxCount")
                Log.d(TAG, "maxHeight1 : $maxHeight")
            }
            if ((maxCount != null) && (maxHeight != null)) {
                Log.d(TAG, "maxCount2 : $maxCount")
                Log.d(TAG, "maxHeight2 : $maxHeight")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                Log.d(TAG, "StartActivity로 들어갈거임")
                applicationContext.startActivity(
                    intent.addFlags(
                        FLAG_ACTIVITY_NEW_TASK
                    )
                )
            }
        }
    }

    private fun getFormattedText(text: String, languageTag: String): String {
        if (showLanguageTag) {
            return String.format(
                TEXT_WITH_LANGUAGE_TAG_FORMAT,
                languageTag,
                text
            )
        }
        return text
    }

    private fun drawText(text: String, rect: RectF, textHeight: Float, canvas: Canvas): Float {
        // If the image is flipped, the left will be translated to right, and the right to left.
        val x0 = translateX(rect.left)
        val x1 = translateX(rect.right)
        rect.left = min(x0, x1)
        rect.right = max(x0, x1)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)
//        canvas.drawRect(rect, rectPaint)
        val textWidth = textPaint.measureText(text)
//        canvas.drawRect(
//            rect.left - STROKE_WIDTH,
//            rect.top - textHeight,
//            rect.left + textWidth + 2 * STROKE_WIDTH,
//            rect.top,
//            labelPaint
//        )
        return textWidth * textHeight
        // Renders the text at the bottom of the box.
//        canvas.drawText(text, rect.left, rect.top - STROKE_WIDTH, textPaint)
    }

    companion object {
        private const val TAG = "TextGraphic"
        private const val TEXT_WITH_LANGUAGE_TAG_FORMAT = "%s:%s"
        private const val TEXT_COLOR = Color.BLACK
        private const val MARKER_COLOR = Color.WHITE
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f

    }

}
