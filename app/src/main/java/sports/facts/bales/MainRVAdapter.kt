package sports.facts.bales

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainRVAdapter(var mainList: ArrayList<String>) :
    RecyclerView.Adapter<MainRVAdapter.MainViewHolder>() {

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item, parent, false)

        return MainViewHolder(itemView)
    }

    override fun getItemCount() = mainList.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.textView.text = mainList[position]
        holder.itemView.setOnClickListener {
            if (holder.textView.maxLines == 3) {
                holder.textView.maxLines = 10
            } else {
                holder.textView.maxLines = 3
            }
        }
    }
}