package inu.withus.restructversion

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import inu.withus.restructversion.databinding.ActivityRegisterFoodBinding
import inu.withus.restructversion.databinding.StandardExpirationdateBinding
import inu.withus.restructversion.dto.FoodInfoDTO
import inu.withus.restructversion.dto.InfoListDTO
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class RegisterFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterFoodBinding
    private lateinit var dlgBinding: StandardExpirationdateBinding
    private var ingredents = arrayOf("사과", "바나나", "당근", "감자", "오이", "파프리카", "양파")
    private var average_exp = arrayOf("2주~1개월", "7일~10일", "2주~1개월", "1개월", "4~5일", "3~4주", "1주~2주")
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    private var place : String = "냉장"
    private var searchExist = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterFoodBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        setContentView(binding.root)

        /* 냉장, 냉동, 실온 터치 이벤트 */

        binding.offButton1.setOnClickListener {
            binding.onButton1.visibility = View.VISIBLE
            binding.onButton2.visibility = View.INVISIBLE
            binding.onButton3.visibility = View.INVISIBLE
            binding.offButton1.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.offButton2.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            binding.offButton3.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            place = binding.offButton1.text.toString()
        }

        binding.offButton2.setOnClickListener {
            binding.onButton1.visibility = View.INVISIBLE
            binding.onButton2.visibility = View.VISIBLE
            binding.onButton3.visibility = View.INVISIBLE
            binding.offButton1.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            binding.offButton2.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.offButton3.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            place = binding.offButton2.text.toString()
        }

        binding.offButton3.setOnClickListener {
            binding.onButton1.visibility = View.INVISIBLE
            binding.onButton2.visibility = View.INVISIBLE
            binding.onButton3.visibility = View.VISIBLE
            binding.offButton1.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            binding.offButton2.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            binding.offButton3.setTextColor(ContextCompat.getColor(this, R.color.black))
            place = binding.offButton3.text.toString()
        }

        binding.standardExpiredate.setOnClickListener {
//            val dlg = StandardExpirationDate(this)
//            dlg.showDialog()
            var builder = AlertDialog.Builder(this)
            builder.setTitle("표준 유통 기한")

            var listener = DialogInterface.OnClickListener { _, which ->
                dlgBinding.foodName.text = "${ingredents[which]}"
                dlgBinding.averageExpirationdate.text = "${average_exp[which]}"
            }
            builder.setItems(ingredents, listener)
            builder.show()
        }

        binding.mButton.setOnClickListener {
//            val intent = Intent(this, LivePreviewActivity::class.java)
            val intent = Intent(this, DetectorActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onResume() {
        super.onResume()

        Log.d(ContentValues.TAG, "RegisterFoodActivity_name에 들어옴!")

        //식품 이름 가져오기 및 저장
        val name : TextView = findViewById(R.id.InputfoodName)
        var resultName = intent.getStringExtra("foodName")
        when(resultName){
            "apple" -> resultName =  "사과"
            "banana" -> resultName =  "바나나"
            "carrot" -> resultName = "당근"
            "potato" -> resultName = "감자"
            "cucumber" ->resultName = "오이"
            "paprika" -> resultName = "파프리카"
            "onion" -> resultName = "양파"
        }
        name.text = resultName

        Log.d(ContentValues.TAG, "RegisterFoodActivity_date에 들어옴!")
        val result : TextView = findViewById(R.id.InputexpireDate)
        var resultExpireDate = intent.getStringExtra("expireDate")


        //유통기한 가져오기 및 저장
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        val calendar = Calendar.getInstance()
        calendar.setTime(date)
        when(resultName){
            "사과" -> {calendar.add(Calendar.DATE, 14)}
            "바나나" -> {calendar.add(Calendar.DATE, 7)}
            "당근" -> {calendar.add(Calendar.DATE, 14)}
            "감자" -> {calendar.add(Calendar.DATE, 30)}
            "오이" -> {calendar.add(Calendar.DATE, 4)}
            "파프리카" -> {calendar.add(Calendar.DATE, 21)}
            "양파" -> {calendar.add(Calendar.DATE, 7)}

        }
        val time = calendar.time
        val formatTime = dateFormat.format(time)
        resultExpireDate = formatTime

        Log.d(ContentValues.TAG, "내일 날짜 ::::::::::::: $formatTime")
        Log.d(ContentValues.TAG, "내일 날짜 ::::::::::::: ${formatTime.javaClass.name}")

        result.text = resultExpireDate



        // 식품 등록 버튼 클릭
        binding.register.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            //places >> count >> count_fridge|count_frozen|count_room
            // 현재 냉장, 냉동, 실온에 저장되어 있는 count수 가져오기?
            val count = firestore?.collection("places")!!.document("count")
            Log.d("TAG","This is count : $count" )
            var foodInfoDTO = FoodInfoDTO()
            var infoListDTO = InfoListDTO()


            // 이미 db에 식품이 존재하는지 확인
            val doc = firestore?.collection(place)!!.document(name.text.toString()).path
            Log.d("TAG", "path : $doc")
            Log.d("TAG", "path : ${doc.substring(3)}")
            if(doc.substring(3) == name.text.toString()) {
                searchExist = searchExist(result, name.text.toString(), place, infoListDTO)
                Log.d(ContentValues.TAG, "1 : $searchExist")
            }


            Log.d(ContentValues.TAG, "searchExist2 : $searchExist")
            if (!searchExist) {
                foodInfoDTO.place = place
                Log.d(ContentValues.TAG, "place = " + foodInfoDTO.place)
                foodInfoDTO.foodName = name.text.toString()
                Log.d(ContentValues.TAG, "name = " + foodInfoDTO.foodName)
                foodInfoDTO.expireDate = listOf(result.text.toString())
                foodInfoDTO.count = listOf(binding.InputCount.text.toString().toInt())
                foodInfoDTO.memo = listOf(binding.InputMemo.text.toString())

                firestore?.collection(place)?.document(foodInfoDTO.foodName!!)?.set(foodInfoDTO)
                    ?.addOnSuccessListener {
                        when (place) {
                            "냉장" -> {
                                count.update("count_fridge", FieldValue.increment(1))
                                Log.d(ContentValues.TAG, "냉장 db 성공")
                            }
                            "냉동" -> {
                                count.update("count_frozen", FieldValue.increment(1))
                                Log.d(ContentValues.TAG, "냉동 db 성공")
                            }
                            "실온" -> {
                                count.update("count_room", FieldValue.increment(1))
                                Log.d(ContentValues.TAG, "실온 db 성공")
                            }
                        }
                    }
            }
            Toast.makeText(this, "저장 완료", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }

        binding.cancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    // 이미 DB에 존재하는 식품일 경우
    private fun searchExist(
        result: TextView,
        foodName: String,
        place: String,
        infoListDTO: InfoListDTO
    ): Boolean {
        infoListDTO.expireDate = result.text.toString()
        infoListDTO.count = binding.InputCount.text.toString().toInt()
        infoListDTO.memo = binding.InputMemo.text.toString()

        Log.d("TAG", "searchExist들어왔음")
        val document = firestore?.collection(place)!!.document(foodName)
        document.update("expireDate", FieldValue.arrayUnion(infoListDTO.expireDate))
        document.update("count", FieldValue.arrayUnion(infoListDTO.count))
        document.update("memo", FieldValue.arrayUnion(infoListDTO.memo))

        return true
    }


    override fun onStop() {
        super.onStop()
    }

}