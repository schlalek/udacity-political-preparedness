package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import timber.log.Timber

class VoterInfoFragment : Fragment() {

    private val viewModel: VoterInfoViewModel by lazy {
        ViewModelProvider(this)[VoterInfoViewModel::class.java]
    }

    private val args: VoterInfoFragmentArgs by navArgs()

    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVoterInfoBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.urlToOpen.observe(viewLifecycleOwner) {
            it?.let {
                val intent = Intent(Intent.ACTION_VIEW, it)
                startActivity(intent)
                viewModel.onUrlOpened()
            }
        }

        Timber.i("Creating VoterInfo with Election ID: ${args.argElectionId}")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getVoterInfo(args.argElectionId, args.argDivision)
    }


}