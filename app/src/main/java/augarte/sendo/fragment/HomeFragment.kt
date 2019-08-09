package augarte.sendo.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import augarte.sendo.R
import augarte.sendo.adapter.WorkoutAdapter
import augarte.sendo.dataModel.Workout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(){

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workoutList : ArrayList<Workout> = ArrayList()
        val w = Workout()
        w.name = "Workout"
        workoutList.add(w)
        workout_rv.layoutManager = LinearLayoutManager(context)
        workout_rv.adapter = WorkoutAdapter(workoutList)

        fab_add.setOnClickListener {
            fab_add.animate().rotation(if (fab_add.rotation==0f) fab_add.rotation+45 else fab_add.rotation-45).start()
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.option_menu, menu)
        menu?.findItem(R.id.search)?.isVisible = true

    }
}