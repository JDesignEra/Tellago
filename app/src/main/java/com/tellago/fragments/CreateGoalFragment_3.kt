package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tellago.R
import com.tellago.interfaces.CreateGoalCommunicator
import com.tellago.models.Goal
import kotlinx.android.synthetic.main.fragment_create_goal_2.view.*
import kotlinx.android.synthetic.main.fragment_create_goal_3.view.*
import java.util.*
import kotlin.collections.ArrayList


class CreateGoalFragment_3 : Fragment() {

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = db.collection("goals")

    // The message received from the previous fragment is shown using the following variable
    var displayMessage1: String? = ""
    var displayMessage2: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_goal_3, container, false)

        view.btn_BackToFragmentTwo.setOnClickListener {
            // Return to previous fragment
            fragmentManager?.popBackStack()
        }


        // Code to submit form & create goal in Firestore
        view.btn_CreateGoal.setOnClickListener {

            createGoalRecord()

        }


        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayMessage1 = "Second Fragment needs to pass the necessary information to Third Fragment"

        displayMessage2 =
                    arguments?.getString("string 2") +
                    arguments?.getInt("careerSelected").toString() +
                    arguments?.getInt("familySelected").toString() +
                    arguments?.getInt("leisureSelected").toString() +
                            "Duration is: " +
                    arguments?.getInt("duration Int").toString() +
                    arguments?.getString("duration String") +
                            "Reminder frequency is: " +
                    arguments?.getInt("reminder Int").toString()


        view.tv_Fragment3Receipt_1.text = displayMessage1
        view.tv_Fragment3Receipt_2.text = displayMessage2

    }

    private fun createGoalRecord()
    {
        Log.d("Create Goal Record", "FIRED")

        val categoryList : MutableList<String> = ArrayList()
        if(arguments?.getInt("careerSelected") == 1)
            categoryList.add("Career")
        if(arguments?.getInt("familySelected") == 1)
            categoryList.add("Family")
        if(arguments?.getInt("leisureSelected") == 1)
            categoryList.add("Leisure")

        var durationInMth : Int = 0
        if(arguments?.getString("duration String") == "mth")
            durationInMth = arguments?.getInt("duration Int")!!
        if(arguments?.getString("duration String") == "yr")
            durationInMth = arguments?.getInt("duration Int")!! * 12

        // Adding durationInMth to current month
        val calendar = Calendar.getInstance()
        val deadlineMonth = calendar.add(Calendar.MONTH, durationInMth)
        Log.d("deadlineMonth", deadlineMonth.toString())


//        val newGoal = Goal(
//            gCreationDate = Timestamp(Date()),
//            gTitle = goalTitle,
//            gFullAmount = goalFullAmount)

        val newGoal = Goal(
            title = arguments?.getString("string 2"),
            category = categoryList,
            targetAmt = 50000,
            uid = user?.uid
        )


        Log.d("Adding to Collection", "FIRED")

        // Inserting record into database
        // (.add(newGoal) allows Firestore to provide unique name for new document)
        collection.add(newGoal)

    }

    companion object {

    }
}