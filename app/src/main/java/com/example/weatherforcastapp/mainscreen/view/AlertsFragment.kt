package com.example.weatherforcastapp.mainscreen.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.database.Repo
import com.example.weatherforcastapp.mainscreen.viewmodel.ViewModelForMainScreen
import com.example.weatherforcastapp.mainscreen.viewmodel.factory
import com.example.weatherforcastapp.model.Alerts
import java.text.SimpleDateFormat
import java.util.*


class AlertsFragment : Fragment() {


    lateinit var viewModel : ViewModelForMainScreen

    lateinit var myCalendarFrom : Calendar
    lateinit var myCalendarTo : Calendar

    lateinit var fromText : EditText
    lateinit var toText   : EditText

    lateinit var notificationRB : CheckBox
    lateinit var enableAlertRB: CheckBox
    lateinit var soundAlertRB : CheckBox

    lateinit var alertRecyclerView: RecyclerView
    lateinit var alertAdapter : AlertAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_alerts, container, false)


        // connect this with his view model
        val repo = Repo()
        val factory = factory(repo)
        viewModel = ViewModelProvider(this,factory)[ViewModelForMainScreen::class.java]

        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar!!.title = "Alerts"

        myCalendarFrom = Calendar.getInstance()
        myCalendarTo = Calendar.getInstance()

        fromText = view.findViewById(R.id.fromTextForAlert)
        toText = view.findViewById(R.id.toTextForAlert)

        val fromDate = OnDateSetListener { view12: DatePicker?, year: Int, month: Int, day: Int ->
            myCalendarFrom.set(Calendar.YEAR, year)
            myCalendarFrom.set(Calendar.MONTH, month)
            myCalendarFrom.set(Calendar.DAY_OF_MONTH, day)
            updateFromLabel()
            val mPrefs: SharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            prefsEditor.putString("FromDate", fromText.text.toString())
            prefsEditor.apply()
            prefsEditor.commit()

        }
        val toDate = OnDateSetListener { view12: DatePicker?, year: Int, month: Int, day: Int ->
            myCalendarTo.set(Calendar.YEAR, year)
            myCalendarTo.set(Calendar.MONTH, month)
            myCalendarTo.set(Calendar.DAY_OF_MONTH, day)
            updateToLabel()
            val mPrefs: SharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            prefsEditor.putString("ToDate", toText.text.toString())
            prefsEditor.apply()
            prefsEditor.commit()
        }

        fromText.setOnClickListener(View.OnClickListener {
            DatePickerDialog(
                requireContext(),
                fromDate,
                myCalendarFrom.get(Calendar.YEAR),
                myCalendarFrom.get(Calendar.MONTH),
                myCalendarFrom.get(Calendar.DAY_OF_MONTH)
            ).show()
        })
        toText.setOnClickListener(View.OnClickListener {
            DatePickerDialog(
                requireContext(),
                toDate,
                myCalendarTo.get(Calendar.YEAR),
                myCalendarTo.get(Calendar.MONTH),
                myCalendarTo.get(Calendar.DAY_OF_MONTH)
            ).show()
        })


        notificationRB = view.findViewById(R.id.notificationButton)
        enableAlertRB = view.findViewById(R.id.alarmButton)
        soundAlertRB = view.findViewById(R.id.styleButton)


        notificationRB.setOnClickListener {
            val mPrefs: SharedPreferences = requireActivity().applicationContext.getSharedPreferences("alertsSP",Context.MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            if(notificationRB.isChecked){
                prefsEditor.putString("notificationRB", "Y")
            }else{
                prefsEditor.putString("notificationRB", "N")
            }
            prefsEditor.apply()
            prefsEditor.commit()

        }

        enableAlertRB.setOnClickListener {
            val mPrefs: SharedPreferences = requireActivity().applicationContext.getSharedPreferences("alertsSP",Context.MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            if(enableAlertRB.isChecked){
                if(fromText.text.isNotEmpty() && toText.text.isNotEmpty()){
                    prefsEditor.putString("enableAlertRB", "Y")
                    viewModel.requestAlertList(this.requireActivity())
                    // ToDo --> we need to clear all the alerts and create the new ones .
                    viewModel.setTheAlarms(this.requireContext(),fromText.text.toString(),toText.text.toString())


                }else{
                    Toast.makeText(this.requireContext(),"fill the interval first",Toast.LENGTH_LONG).show()
                    enableAlertRB.isChecked = false
                }

            }else{
                fromText.setText("")
                toText.setText("")
                prefsEditor.putString("enableAlertRB", "N")

                // ToDo --> we need to clear all the alerts
                viewModel.cancelAllAlarms(this.requireContext())

            }
            prefsEditor.apply()
            prefsEditor.commit()
        }

        soundAlertRB.setOnClickListener {
            val mPrefs: SharedPreferences = requireActivity().applicationContext.getSharedPreferences("alertsSP",Context.MODE_PRIVATE)
            val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
            if(soundAlertRB.isChecked){
                prefsEditor.putString("soundAlertRB", "Y")
            }else{
                prefsEditor.putString("soundAlertRB", "N")
            }
            prefsEditor.apply()
            prefsEditor.commit()
        }



        // set the recycle
        alertRecyclerView = view.findViewById(R.id.alertRecycleView)
        alertRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this.requireContext())
        layoutManager.orientation = RecyclerView.VERTICAL
        alertRecyclerView.layoutManager = layoutManager
        val list : List<Alerts> = listOf()
        alertAdapter = AlertAdapter(list)
        alertRecyclerView.adapter = alertAdapter



        // logic for showing this screen (loading it's saved info)

        val mPrefs: SharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        // load dates
        if(mPrefs.getString("FromDate","NA") != "NA"){
            fromText.setText(mPrefs.getString("FromDate","NA"))
        }
        if(mPrefs.getString("ToDate","NA") != "NA"){
            toText.setText(mPrefs.getString("ToDate","NA"))
        }

        // load radio buttons

        val mPrefsX: SharedPreferences = requireActivity().applicationContext.getSharedPreferences("alertsSP",Context.MODE_PRIVATE)
        if(mPrefsX.getString("notificationRB","NA") == "Y"){
            notificationRB.isChecked = true
        }
        if(mPrefsX.getString("enableAlertRB","NA") == "Y"){
            enableAlertRB.isChecked = true
        }else{
            fromText.setText("")
            toText.setText("")
        }
        if(mPrefsX.getString("soundAlertRB","NA") == "Y"){
            soundAlertRB.isChecked = true
        }


        // load the recycle view with data
        if(mPrefsX.getString("enableAlertRB","NA") == "Y"){
            // then we need to load the alerts from the screen
            viewModel.requestAlertList(this.requireActivity())
        }

        // observer on the answer
        viewModel.alertAnswer.observe(viewLifecycleOwner){
            //re-fresh the alert list
            if(!it.listOfAlerts.isNullOrEmpty()){
                alertAdapter.list = it.listOfAlerts
                alertAdapter.notifyDataSetChanged()
            }



        }


        return view
    }

    private fun updateFromLabel() {
        val myFormat = "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        fromText.setText(dateFormat.format(myCalendarFrom.time))
    }

    private fun updateToLabel() {
        val myFormat = "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        toText.setText(dateFormat.format(myCalendarTo.time))
    }


}