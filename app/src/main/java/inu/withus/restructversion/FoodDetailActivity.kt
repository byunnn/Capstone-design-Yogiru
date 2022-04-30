package inu.withus.restructversion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import inu.withus.restructversion.R
import inu.withus.restructversion.databinding.ActivityFoodDetailBinding
import inu.withus.restructversion.dto.FoodInfoDTO
import android.app.AlertDialog;
import inu.withus.restructversion.dto.FoodDTO

class FoodDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFoodDetailBinding
    lateinit var fdRecyclerAdaper: FoodDetailRecyclerAdapter
    private lateinit var foodDetailData : FoodInfoDTO
    private val foodDeatilDatas = mutableListOf<FoodDTO>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)


        val intent : Intent = getIntent()
        foodDetailData = intent.getParcelableExtra("data")!!
        //var datas : FoodInfoDTO? = intent.getParcelableExtra("data")
        Log.d("TAG", "받아온 데이터 ___________ +$foodDetailData")

        val text : TextView = findViewById(R.id.foodName)
        text.text = foodDetailData?.foodName

        val dataLen = (foodDetailData.expireDate)?.size!!
        Log.d("TAG", "data 길이 : $dataLen")

        for(i in 0..dataLen-1){
            val data = FoodDTO(
                foodName = foodDetailData.foodName!!,
                expireDate = foodDetailData.expireDate!!.get(i),
                count = foodDetailData.count!!.get(i)
            )
            foodDeatilDatas.add(data)
        }
        initRecyclerview()
    }

    fun foodDataDelete(){
        // 파이어베이스에 카운트 개수 줄이기
        
    }

    fun foodDataDeleteAll(){

        // 유통기한이 같은 한 식품 전체 삭제 ex) 2022-02-01까지인 사과가 3개 있다면 3개 동시에 삭제
    }


    //recyclerview 초기 설정
    private fun initRecyclerview() {
        fdRecyclerAdaper = FoodDetailRecyclerAdapter()
        fdRecyclerAdaper.replaceList(foodDeatilDatas)

        val foodDetailRecyclerView : RecyclerView = findViewById(R.id.foodDetailRecyclerView)
        foodDetailRecyclerView.adapter = fdRecyclerAdaper
        foodDetailRecyclerView.layoutManager = LinearLayoutManager(this)


    }



}