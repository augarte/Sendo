package augarte.sendo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import augarte.sendo.R
import augarte.sendo.activity.MainActivity
import augarte.sendo.activity.WorkoutDayActivity
import augarte.sendo.adapter.CreateWorkoutAdapter
import augarte.sendo.adapter.ExerciseChooseAdapter
import augarte.sendo.dataModel.Exercise
import augarte.sendo.dataModel.ExerciseDay
import augarte.sendo.dataModel.Serie
import augarte.sendo.database.SelectTransactions
import kotlinx.android.synthetic.main.bottomsheet_add_progress.*
import kotlinx.android.synthetic.main.bottomsheet_choose_exercise.*
import kotlinx.android.synthetic.main.item_exercise_day.*

class AddProgressFragment(private val exerciseDay: ExerciseDay, private val listener: WorkoutDayActivity.OnAddProgress) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottomsheet_add_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title_exercise.text = exerciseDay.exercise!!.name
        serieET.setText(exerciseDay.serieNum.toString())
        repET.setText(exerciseDay.repNum.toString())
        weightET.setText(if (exerciseDay.series.isNotEmpty()) exerciseDay.series.last().weight.toString() else "")

        insertButton.setOnClickListener {
            val newSerie = Serie()
            newSerie.exerciseDayId = exerciseDay.id
            newSerie.repetitions = repET.text.toString().toInt()
            newSerie.weight = weightET.text.toString().toDouble()
            exerciseDay.series.add(newSerie)

            MainActivity.dbHandler.insertSerie(newSerie)
            listener.onSerieAdded()
        }
    }
}