package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment : Fragment() {

    private val viewModel: ElectionsViewModel by lazy {
        ViewModelProvider(this)[ElectionsViewModel::class.java]
    }

    private lateinit var binding: FragmentElectionBinding

    private val electionSavedAdapter =
        ElectionListAdapter(ElectionListener { viewModel.onElectionClicked(it) })

    private val electionUpcomingAdapter =
        ElectionListAdapter(ElectionListener { viewModel.onElectionClicked(it) })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
            : View {
        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        // TODO: Link elections to voter info

        // TODO: Initiate recycler adapters
        binding.recyclerUpcoming.adapter = electionUpcomingAdapter
        binding.recyclerSaved.adapter = electionSavedAdapter

        // TODO: Populate recycler adapters
        return binding.root
    }

    // TODO: Refresh adapters when fragment loads
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            it.apply {
                electionUpcomingAdapter.submitList(this)
            }
        }
        viewModel.savedElections.observe(viewLifecycleOwner) {
            it.apply {
                electionSavedAdapter.submitList(this)
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}