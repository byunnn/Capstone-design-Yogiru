package inu.withus.restructversion

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import inu.withus.restructversion.databinding.ActivityMainBinding
import inu.withus.restructversion.dto.FoodInfoDTO
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var recyclerAdaper: RecyclerAdapter
//    private val datas = mutableListOf<FoodData>()
    private val datas = mutableListOf<FoodInfoDTO>()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 최조 실행 시에만 해야 할 작업들을 작성
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Initialize And Assign Variable
        val bottomNavigation = binding.bottomNavigation
        setContentView(binding.root)

        // Set Home Selected
        bottomNavigation.selectedItemId = R.id.home

        // Perform ItemSelectedListener
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.store -> startActivity(Intent(this, StoreActivity::class.java))
                R.id.shoppinglist -> startActivity(Intent(this, ShoppingListActivity::class.java))
                R.id.home -> startActivity(Intent(this, MainActivity::class.java))
                R.id.recipe -> startActivity(Intent(this, RecipeActivity::class.java))
                R.id.setting -> startActivity(Intent(this, SettingActivity::class.java))
            }
            true
        }

        val docRef = firestore.collection("places")!!.document("count")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // 냉장, 냉동, 실온 각각에 저장되어 있는 식품의 개수를 가져와서 보여준다.
                    countData(document)
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with", exception)
            }

        binding.addFood.setOnClickListener {
            val intent = Intent(this, RegisterFoodActivity::class.java)
            startActivity(intent)
        }

        val slidePanel = binding.slidingLayout
        val location = binding.location
        val quantity = binding.quantity


        //냉장, 냉동, 실온 버튼 클릭 시 리스트 출력
        binding.fridge.setOnClickListener{
            location.text = "냉장"
            quantity.text = binding.itemCount1.text
            initRecyclerData(location.text, "expireDate")
            if (slidePanel.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.anchorPoint = 0.7f
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }

            binding.sortByAlphabet.setOnClickListener{
                location.text = "냉장"
                quantity.text = binding.itemCount3.text
                initRecyclerData(location.text, "foodName")
            }

            binding.sortByDateAsc.setOnClickListener{
                location.text = "냉장"
                quantity.text = binding.itemCount3.text
                initRecyclerData(location.text, "expireDate" )
            }

            binding.sortByDateDesc.setOnClickListener{
                location.text = "냉장"
                quantity.text = binding.itemCount3.text
                initRecyclerData(location.text, "expireDate", Query.Direction.DESCENDING)
            }

        }


        binding.frozen.setOnClickListener{
            location.text = "냉동"
            quantity.text = binding.itemCount2.text
            initRecyclerData(location.text, "expireDate")
            if (slidePanel.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.anchorPoint = 0.7f
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }

            binding.sortByAlphabet.setOnClickListener{
                location.text = "냉동"
                quantity.text = binding.itemCount3.text
                initRecyclerData(location.text, "foodName")
            }

            binding.sortByDateAsc.setOnClickListener{
                location.text = "냉동"
                quantity.text = binding.itemCount3.text
                initRecyclerData(location.text, "expireDate")
            }

            binding.sortByDateDesc.setOnClickListener{
                location.text = "냉동"
                quantity.text = binding.itemCount3.text
                initRecyclerData(location.text, "expireDate", Query.Direction.DESCENDING)
            }


        }

        binding.room.setOnClickListener{
            location.text = "실온"
            quantity.text = binding.itemCount3.text
            initRecyclerData(location.text, "expireDate")
            if (slidePanel.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.anchorPoint = 0.7f
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }

            binding.sortByAlphabet.setOnClickListener{
                location.text = "실온"
                quantity.text = binding.itemCount3.text
                initRecyclerData(location.text, "foodName")
            }

            binding.sortByDateAsc.setOnClickListener{
                location.text = "실온"
                quantity.text = binding.itemCount3.text
                initRecyclerData(location.text, "expireDate")
            }

            binding.sortByDateDesc.setOnClickListener{
                location.text = "실온"
                quantity.text = binding.itemCount3.text
                initRecyclerData(location.text, "expireDate", Query.Direction.DESCENDING)
            }


        }
    }


    //recyclerview 초기 설정
    private fun initRecyclerview() {
        recyclerAdaper = RecyclerAdapter(this)
        recyclerAdaper.replaceList(datas)
        Log.d("TAG", "init recyclerview as $datas")
        binding.recyclerView.adapter = recyclerAdaper
        binding.recyclerView.layoutManager = LinearLayoutManager(this)


    }


    // recyclerview data 설정
    private fun initRecyclerData(location: CharSequence, sort :String, option : Query.Direction=Query.Direction.ASCENDING) {
        // DB에서 식품 리스트를 가지고 오는 부분
        Log.d(ContentValues.TAG, "들어옴!")

        val docRef = firestore?.collection("냉장").document("가지")
        docRef.get().addOnSuccessListener { documentSnapshot ->
            removeAllData()

            //        firestore?.collection("냉장")
//            .orderBy(sort, option)
//            .get()
//            .addOnSuccessListener { documents ->
//                removeAllData()
//                for (document in documents) {
//                    val city = document.toObject<FoodInfoDTO>()
//                    Log.d("TAG", "data : $city")
//                }
//            }

            val testData = documentSnapshot.toObject<FoodInfoDTO>()!!
            Log.d("TAG", "data : $testData")
            datas.add(testData)
            Log.d("TAG", "Data : $datas")
        }

        initRecyclerview()
    }




    private fun removeAllData(){
        datas.clear()
        Log.d(ContentValues.TAG, "REMOVE : $datas")
    }


    private fun countData(document: DocumentSnapshot) {
        binding.itemCount1.text = document["count_fridge"].toString()
        binding.itemCount2.text = document["count_frozen"].toString()
        binding.itemCount3.text = document["count_room"].toString()
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    // 뒤로가기 버튼 이벤트
    override fun onBackPressed() {
        val slidePanel = binding.slidingLayout
        val slideCollapsed = SlidingUpPanelLayout.PanelState.COLLAPSED
        val slideAnchored = SlidingUpPanelLayout.PanelState.ANCHORED
        val slideExpanded = SlidingUpPanelLayout.PanelState.EXPANDED

        if (slidePanel.panelState == slideExpanded || slidePanel.panelState == slideAnchored ) {
            slidePanel.panelState = slideCollapsed
        }

        else
            finish()
    }


}


