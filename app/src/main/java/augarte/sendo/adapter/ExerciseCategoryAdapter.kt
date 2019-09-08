package augarte.sendo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import augarte.sendo.R
import augarte.sendo.dataModel.Exercise
import com.google.android.material.snackbar.Snackbar
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import kotlinx.android.synthetic.main.item_exercise.view.*
import kotlinx.android.synthetic.main.item_exercise.view.exercise_name
import kotlinx.android.synthetic.main.item_exercise_category.view.*

class ExerciseCategoryAdapter(private val items : MutableList<Exercise>) : RecyclerView.Adapter<ExerciseCategoryAdapter.MainViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    var onItemClick: ((Pair<Exercise, View>) -> Unit)? = null

    private var removedPosition: Int = -1
    private lateinit var removedItem: Exercise
    private var lastCategory = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_exercise_category, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]

        if (lastCategory != item.type!!.id){
            holder.categoryTitle.text = item.type!!.name
            holder.categoryLayout.visibility = View.VISIBLE
        }
        lastCategory = item.type!!.id!!

        holder.exerciseName.text = item.name
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getSectionName(position: Int): String {
        val item = items[position]
        return if (item.name!!.trim().isNotEmpty()) item.name!![0] +""
        else ""
    }

    fun removeWithSwipe(viewHolder: RecyclerView.ViewHolder) {
        removedPosition = viewHolder.adapterPosition
        removedItem = items[viewHolder.adapterPosition]
        items.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)

        Snackbar.make(viewHolder.itemView, "$removedItem.name deleted.", Snackbar.LENGTH_LONG).setAction("UNDO") {
            items.add(removedPosition, removedItem)
            notifyItemInserted(removedPosition)
        }.show()
    }

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var exerciseName: TextView = view.exercise_name
        var categoryLayout: LinearLayout = view.category_layout
        var categoryTitle: TextView = view.category_title
    }
}