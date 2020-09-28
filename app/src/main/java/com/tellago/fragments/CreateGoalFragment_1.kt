package com.tellago.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tellago.R
import com.tellago.interfaces.CreateGoalCommunicator
import kotlinx.android.synthetic.main.fragment_create_goal_1.view.*
import kotlinx.android.synthetic.main.fragment_create_goal_one.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateGoalFragment_1.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateGoalFragment_1 : Fragment() {

    private lateinit var communicator: CreateGoalCommunicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_goal_1, container, false)
        communicator = activity as CreateGoalCommunicator

        view.btn_ToFragmentTwo.setOnClickListener {
            // use communicator to pass String data from textview_FragmentOne
            // communicator.passStringDataComOne(view.et_FragmentOne.text.toString())
            // key is defined within this function
            // bundle.putString("string 1", editTextStringInput) --> 'string 1' is the key
            Log.d("Next Fragment BTN", "FIRED")
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateGoalFragment_1.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateGoalFragment_1().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}