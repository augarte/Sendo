package augarte.sendo.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import augarte.sendo.R
import augarte.sendo.activity.MainActivity
import augarte.sendo.dataModel.Exercise
import augarte.sendo.database.DatabaseConstants
import augarte.sendo.fragment.ExerciseInfoDialogFragment
import augarte.sendo.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
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

        if (position == 0 || items[position-1].type!!.id != item.type!!.id){
            holder.categoryTitle.text = item.type!!.name
            holder.categoryLayout.visibility = View.VISIBLE
        }
        else{
            holder.categoryLayout.visibility = View.GONE
        }
        lastCategory = item.type!!.id!!

        holder.exerciseName.text = item.name
        Glide.with(holder.exerciseImage.context).load(item.imageURL).placeholder(R.drawable.ic_sendo_placeholder).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(holder.exerciseImage)

        holder.itemView.setOnClickListener {
            val manager = (holder.itemView.context as FragmentActivity).supportFragmentManager
            val exerciseInforDialogFragment = ExerciseInfoDialogFragment(item)
            exerciseInforDialogFragment.show(manager, "DIALOG")
        }

        if (item.favorite) {
            holder.fav.setImageResource(R.drawable.ic_star_full)
            ImageViewCompat.setImageTintList(holder.fav, ColorStateList.valueOf(ContextCompat.getColor(holder.fav.context, R.color.starYellow)))
        } else {
            holder.fav.setImageResource(R.drawable.ic_star_empty)
            ImageViewCompat.setImageTintList(holder.fav, ColorStateList.valueOf(Utils.getColorFromAttr(holder.fav.context, R.attr.primaryText)))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getSectionName(position: Int): String {
        val item = items[position]
        return if (item.type!!.name!!.trim().isNotEmpty()) item.type!!.name!!.substring(0,1)
        else ""
    }

    fun removeWithSwipe(context: Context, viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.adapterPosition
        removedPosition = position
        removedItem = items[position]
        removedItem.state = DatabaseConstants.STATE_ARCHIVED
        MainActivity.dbHandler.updateExerciseState(removedItem)
        items.removeAt(position)
        notifyItemRemoved(position)

        Snackbar.make(viewHolder.itemView, context.getString(R.string.sendo_snackbar_archived_exercise, removedItem.name.toString()), Snackbar.LENGTH_LONG).setAction(context.getString(R.string.sendo_snackbar_undo)) {
            removedItem.state = DatabaseConstants.STATE_ACTIVE
            MainActivity.dbHandler.updateExerciseState(removedItem)
            items.add(removedPosition, removedItem)
            notifyItemInserted(removedPosition)
        }.show()
    }

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var exerciseName: TextView = view.exercise_name
        var categoryLayout: LinearLayout = view.category_layout
        var categoryTitle: TextView = view.category_title
        var exerciseImage: ImageView = view.exercise_image
        var fav: ImageView = view.fav
    }
}