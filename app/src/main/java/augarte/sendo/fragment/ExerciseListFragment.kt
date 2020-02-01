package augarte.sendo.fragment

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import augarte.sendo.R
import augarte.sendo.activity.MainActivity
import augarte.sendo.adapter.ExerciseArchivedAdapter
import augarte.sendo.adapter.ExerciseCategoryAdapter
import augarte.sendo.adapter.ExerciseListAdapter
import augarte.sendo.dataModel.Exercise
import augarte.sendo.database.SelectTransactions
import com.simplecityapps.recyclerview_fastscroll.utils.Utils
import kotlinx.android.synthetic.main.fragment_exercise_list.*

class ExerciseListFragment : Fragment() {

    private lateinit var swipeBackground: ColorDrawable
    private lateinit var deleteIcon : Drawable
    private lateinit var exerciseList : MutableList<Exercise>
    private lateinit var exerciseAdapter: Any
    private lateinit var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback
    private var hasSearch = true;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exercise_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exerciseList = MainActivity.dbHandler.getExercise(SelectTransactions.SELECT_ALL_EXERCISE_ORDER_NAME, null).toMutableList()

        exerciseAdapter = ExerciseListAdapter(exerciseList)
        exercise_list.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = exerciseAdapter as ExerciseListAdapter
        }

        /*val listener = object: OnDialogClickListener {
            override fun onDialogAccept(dialog: DialogFragment) {
                (exerciseAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>).notifyDataSetChanged()
                dialog.dismiss()
            }
            override fun onDialogDismiss() {
                fab_add.animate().rotation(if (fab_add.rotation==0f) fab_add.rotation+45 else fab_add.rotation-45).start()
            }
        }

        val fab : FloatingActionButton = view.findViewById(R.id.fab_add)
        fab.setOnClickListener {
            val addExerciseDialog = AddExerciseDialogFragment(listener)
            addExerciseDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog)
            if(!addExerciseDialog.isAdded){
                addExerciseDialog.show(fragmentManager!!, addExerciseDialog.tag)
                fab_add.animate().rotation(if (fab_add.rotation==0f) fab_add.rotation+45 else fab_add.rotation-45).start()
            }
        }*/

        configureSwipeDelete()
        checkEmptyExerciseList()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.option_menu, menu)
        menu.findItem(R.id.group)?.isVisible = true
        val search = menu.findItem(R.id.search)

        search?.isVisible = true
        val searchView = search.actionView as SearchView
        search(searchView)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search).isVisible = hasSearch
    }

    private fun search(searchView: SearchView){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filterText = newText.toLowerCase()
                val newList = ArrayList<Exercise>()
                for (exercise in exerciseList) {
                    if (exercise.name?.toLowerCase()?.contains(filterText) == true || exercise.type?.name?.toLowerCase()?.contains(filterText) == true) {
                        newList.add(exercise)
                    }
                }
                if (exerciseAdapter is ExerciseListAdapter) (exerciseAdapter as ExerciseListAdapter).setFilter(newList)
                if (exerciseAdapter is ExerciseArchivedAdapter) (exerciseAdapter as ExerciseArchivedAdapter).setFilter(newList)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.list -> {
                hasSearch = true
                activity?.invalidateOptionsMenu()
                exerciseList = MainActivity.dbHandler.getExercise(SelectTransactions.SELECT_ALL_EXERCISE_ORDER_NAME, null).toMutableList()
                exercise_list.setPopupTextSize(Utils.toScreenPixels(resources, 44f))
                exerciseAdapter = ExerciseListAdapter(exerciseList)
                exercise_list.adapter = exerciseAdapter as ExerciseListAdapter
            }
            R.id.fav -> {
                hasSearch = true
                activity?.invalidateOptionsMenu()
                exerciseList = MainActivity.dbHandler.getExercise(SelectTransactions.SELECT_STARRED_EXERCISES, null).toMutableList()
                exercise_list.setPopupTextSize(Utils.toScreenPixels(resources, 44f))
                exerciseAdapter = ExerciseListAdapter(exerciseList)
                exercise_list.adapter = exerciseAdapter as ExerciseListAdapter
            }
            R.id.category -> {
                hasSearch = false
                activity?.invalidateOptionsMenu()
                exerciseList = MainActivity.dbHandler.getExercise(SelectTransactions.SELECT_ALL_EXERCISE_ORDER_TYPE, null).toMutableList()
                exercise_list.setPopupTextSize(Utils.toScreenPixels(resources, 44f))
                exerciseAdapter = ExerciseCategoryAdapter(exerciseList)
                exercise_list.adapter = exerciseAdapter as ExerciseCategoryAdapter
            }
            R.id.archived -> {
                hasSearch = true
                activity?.invalidateOptionsMenu()
                exerciseList = MainActivity.dbHandler.getExercise(SelectTransactions.SELECT_ARCHIVED_EXERCISES_ORDER_NAME, null).toMutableList()
                exercise_list.setPopupTextSize(Utils.toScreenPixels(resources, 44f))
                exerciseAdapter = ExerciseArchivedAdapter(exerciseList)
                exercise_list.adapter = exerciseAdapter as ExerciseArchivedAdapter
            }
            else -> return super.onOptionsItemSelected(item)
        }
        configureSwipeDelete()
        checkEmptyExerciseList()
        return true
    }

    private fun configureSwipeDelete(){
        if (exerciseAdapter is ExerciseArchivedAdapter) {
            swipeBackground = ColorDrawable(augarte.sendo.utils.Utils.getColorFromAttr(context, R.attr.ExerciseUnarchiveBackground))
            deleteIcon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_unarchive) }!!
        } else{
            swipeBackground = ColorDrawable(augarte.sendo.utils.Utils.getColorFromAttr(context, R.attr.ExerciseArchiveBackground))
            deleteIcon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_archive) }!!
        }
        deleteIcon.setTint(augarte.sendo.utils.Utils.getColorFromAttr(context, R.attr.ExerciseArchiveIcon))

        itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                Handler().postDelayed({
                    when (exerciseAdapter) {
                        is ExerciseListAdapter -> (exerciseAdapter as ExerciseListAdapter).removeWithSwipe(context ?: requireContext(), viewHolder)
                        is ExerciseCategoryAdapter -> (exerciseAdapter as ExerciseCategoryAdapter).removeWithSwipe(context ?: requireContext(), viewHolder)
                        is ExerciseArchivedAdapter -> (exerciseAdapter as ExerciseArchivedAdapter).removeWithSwipe(context ?: requireContext(), viewHolder)
                    }
                }, 200)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                if (dX < 0) {
                    itemView.background = ContextCompat.getDrawable(context ?: requireContext(), R.drawable.background_exercise_swipe)
                    swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMargin - deleteIcon.intrinsicWidth, itemView.top + iconMargin, itemView.right - iconMargin, itemView.bottom - iconMargin)
                }
                else {
                    itemView.background = ContextCompat.getDrawable(context!!, R.drawable.background_exercise_noswipe)
                }
                swipeBackground.draw(c)
                c.save()

                if(dX < 0){
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                }
                deleteIcon.draw(c)
                c.restore()

                //if(-dX < itemView.height+(iconMargin/2)){
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                //}
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(exercise_list)
    }

    private fun checkEmptyExerciseList(){
        if (exerciseList.isEmpty()) {
            when (exerciseAdapter){
                is ExerciseListAdapter,
                is ExerciseCategoryAdapter -> {
                    no_exercise_icon.setImageResource(R.drawable.ic_weightlifting)
                    no_exercise_text.text = getString(R.string.sendo_no_exercise)
                }
                is ExerciseArchivedAdapter -> {
                    no_exercise_icon.setImageResource(R.drawable.ic_archive)
                    no_exercise_text.text = getString(R.string.sendo_no_archived_exercise)
                }
            }
            exercise_list.visibility = View.GONE
            no_exercise_layout.visibility = View.VISIBLE
        } else {
            exercise_list.visibility = View.VISIBLE
            no_exercise_layout.visibility = View.GONE
        }
    }

    interface OnDialogClickListener {
        fun onDialogAccept(dialog: DialogFragment)
        fun onDialogDismiss()
    }
}