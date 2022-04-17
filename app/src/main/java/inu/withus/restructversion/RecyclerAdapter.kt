package inu.withus.restructversion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import inu.withus.restructversion.FoodData
import inu.withus.restructversion.R
import inu.withus.restructversion.databinding.ActivityItemBinding

class RecyclerAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private lateinit var binding: ActivityItemBinding
    var datas = mutableListOf<FoodData>()


    //onCreateViewHolder : 어떤 목록 레이아웃을 만들 것인지 반환(어떤 뷰를 생성할 것인가)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_item,parent,false)
        return ViewHolder(view)
    }


    //getItemCount : 몇 개의 목록을 만들지를 반환
    override fun getItemCount(): Int = datas.size


    //onBindViewHolder : 생성된 뷰(껍데기)에 무슨 데이터를 넣을 것인가
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    fun replaceList(newList: MutableList<FoodData>) {
        datas = newList.toMutableList()
        //어댑터의 데이터가 변했다는 notify를 날린다
        notifyDataSetChanged()
    }

    //ViewHolder : 목록의 개별 항목 레이아웃을 포함하는 View 래퍼로, 각 목록 레이아웃에 필요한 기능들을 구현하는 공간
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val foodName: TextView = itemView.findViewById(R.id.item_foodName)
        private val expireDate: TextView = itemView.findViewById(R.id.item_expireDate)
        private val quantity: TextView = itemView.findViewById(R.id.item_quantity)

        fun bind(item: FoodData) {
            foodName.text = item.foodName
            expireDate.text = item.expireDate.toString()
            quantity.text = item.quantity.toString()

        }
    }


}