package com.tellago.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.tellago.R


class ConfirmEditProfileFragment : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_edit_profile, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            // Open alert message
            val builder : AlertDialog.Builder? = this?.let {
                AlertDialog.Builder(this.requireContext())
            }

            builder?.setMessage("Are you sure you want to edit your profile?")
                ?.setTitle("Confirm Edit")
                ?.apply{
                    // Setting positive & negative buttons
                    setPositiveButton(R.string.confirm,
                    DialogInterface.OnClickListener { dialog, which ->
                        // Send the positive button event back to the host activity
                        listener.onDialogPositiveClick(this@ConfirmEditProfileFragment)
                    })
                setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, which ->
                        // Send the negative button event back to the host activity
                        listener.onDialogNegativeClick(this@ConfirmEditProfileFragment)
                    })
            }

            // Create the AlertDialog
            builder?.create()
        }!!
    }

    // Use this instance of the interface to deliver action events
    private lateinit var listener : NoticeDialogListener

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

}