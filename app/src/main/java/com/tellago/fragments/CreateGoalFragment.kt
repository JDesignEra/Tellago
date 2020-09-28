package com.tellago.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.tellago.R
import com.tellago.models.Goal
import com.tellago.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_create_goal.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateGoalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateGoalFragment : Fragment() {

    private lateinit var fragmentUtils: FragmentUtils
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = db.collection("goals")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentUtils = FragmentUtils(
            requireActivity().supportFragmentManager,
            R.id.fragment_container
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_goal, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //firebaseFirestore = FirebaseFirestore.getInstance()

        // Query
        //val query : Query = firebaseFirestore.collection("goals")


        configureToolbar()

        btn_Create_New_Goal.setOnClickListener {
            createGoalRecord()
        }

    }

    private fun configureToolbar() {
        toolbar_create_goal.setNavigationIcon(R.drawable.ic_cancel_black_48)
        toolbar_create_goal.setNavigationOnClickListener {
            // Allow user to return to previous fragment in the Stack
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGoalRecord()
    {
        Log.d("Create Goal Record", "FIRED")

        val goalTitle : String = et_goal_title.text.toString()
        val goalFullAmount : Long = et_goal_fullAmount.text.toString().toLong()


        Log.d("Input #1:", goalTitle)
        Log.d("Input #2:", goalFullAmount.toString())

//        val newGoal = Goal(
//            gCreationDate = Timestamp(Date()),
//            gTitle = goalTitle,
//            gFullAmount = goalFullAmount)

        Log.d("Adding to Collection", "FIRED")

        // Inserting record into database
        // (.add(newGoal) allows Firestore to provide unique name for new document)
//        db.collection("goals")
//            .add(newGoal)

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateGoalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateGoalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}



