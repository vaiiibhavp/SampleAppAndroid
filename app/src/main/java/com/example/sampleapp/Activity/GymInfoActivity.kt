package com.example.sampleapp.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleapp.adapter.GymTagAdapter
import com.example.sampleapp.databinding.ActivityGymInfoBinding
import com.example.sampleapp.viewModal.GymInfoViewModel
import com.google.android.flexbox.FlexboxLayoutManager


class GymInfoActivity : AppCompatActivity() {

    private val binding: ActivityGymInfoBinding by lazy {
        ActivityGymInfoBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel: GymInfoViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(GymInfoViewModel::class.java)
    }

    private var adapter: RecyclerView.Adapter<*>? = null
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent?.extras?.let {
            date = it.getString("ID")
        }

        binding.recyGymTagList.setHasFixedSize(true)
        val layoutManager = FlexboxLayoutManager(this)
        binding.recyGymTagList.setLayoutManager(layoutManager)

        viewModel.let {
            it.getGymInfoDetail(date!!, this)
            it.gymInfoDetailData.observeForever { data ->
                val stringArray: List<String> = data.gym!!.Amenities!!.split(",")
                adapter = GymTagAdapter(this, stringArray)
                binding.recyGymTagList.setAdapter(adapter)
                binding.textViewName.text = data.gym!!.Name
                binding.textStreetName.text = data.gym!!.City + ", " + data.gym!!.State + ", " + data.gym!!.Country
                binding.txtAddress.text = data.gym!!.Address

            }
        }
    }
}