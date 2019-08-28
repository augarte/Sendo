package augarte.sendo.adapter

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import augarte.sendo.*
import augarte.sendo.dataModel.Day
import augarte.sendo.dataModel.Exercise
import kotlinx.android.synthetic.main.activity_create_workout.*
import kotlinx.android.synthetic.main.item_day_card.view.*

class CreateWorkoutAdapter(private val items : ArrayList<Day>, val rv : RecyclerView) : RecyclerView.Adapter<CreateWorkoutAdapter.ViewHolder>() {

    var newItem : Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_day_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.card_title?.text = item.name.toString()
        holder.exercise_list.text = createExerciseList(item.getExercises())

        var listener = View.OnClickListener {
            if (holder.arrow.rotation == 0f) {
                Animations.rotate(holder.arrow, 180f)
                Animations.toggleCardBody(holder.card, 80.toPx, 180.toPx)
            } else {
                Animations.rotate(holder.arrow, 0f)
                Animations.toggleCardBody(holder.card, 180.toPx, 80.toPx)
            }
        }

        holder.card.setOnClickListener{
            holder.arrow.performClick()
            holder.arrow.isPressed = true
            holder.arrow.invalidate()
            holder.arrow.isPressed = false
            holder.arrow.invalidate()
        }
        holder.arrow.setOnClickListener(listener)

        holder.edit.setOnClickListener{
            //val intent = Intent()
            //context.startActivity(new Intent(activity, NVirementEmmeteur.class));
        }

        if (position!=0 && !newItem)  {
            holder.itemView.z -= 1*position
            holder.itemView.y = ((-100)*position).toPxF
            holder.itemView.animate().setDuration(300).translationYBy((100*position).toPxF).zBy(1f*position)
            newItem = false
        }
        else if (newItem) {
            holder.itemView.z -= 1
            holder.itemView.y = ((-100)).toPxF
            rv.post{holder.itemView.animate().setDuration(400).translationYBy((100).toPxF).zBy(1f)}
        }
    }

    fun addItem(pos: Int, d: Day) {
        newItem = true
        rv.post{
            items.add(d)
            notifyItemInserted(items.size)
        }
    }

    fun deleteItem(pos : Int){
        val view = rv.layoutManager!!.getChildAt(pos)
        if(view != null){
            if (view!!.drop_arrow.rotation != 0f) {
                view.drop_arrow?.animate()?.rotation(0f)
                Animations.toggleCardBody(view.card as View, 180.toPx, 80.toPx)
            }
            view.z -= 1
            view.animate()?.translationYBy((-100).toPxF)?.setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (items.size>pos){
                        view.animate()?.setDuration(0)?.alpha(0f)?.translationYBy((100).toPxF)
                        rv.post{
                            items.removeAt(pos)
                            notifyItemRemoved(items.size)
                        }
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
        else {
            items.removeAt(pos)
            notifyItemChanged(pos)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun createExerciseList(exercises : ArrayList<Exercise>) : String {
        var exerciseListString = ""
        for (e in exercises) {
            exerciseListString += if (e == exercises[0]) e.name
            else "\n" + e.name

        }
        return exerciseListString
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val card : CardView = view.card
        val card_title : TextView = view.card_title
        val exercise_list : TextView = view.exercise_list
        val arrow : ImageButton = view.drop_arrow
        val edit : ImageButton = view.edit

    }
}