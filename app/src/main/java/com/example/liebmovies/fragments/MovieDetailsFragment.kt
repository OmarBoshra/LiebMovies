package com.example.liebmovies.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.liebmovies.R
import com.example.liebmovies.databinding.FragmentMovieDetailsBinding
import com.example.liebmovies.domains.MyMovieDetails


private const val MOVIEDETAILS = "MovieDetails"

class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieDetails: MyMovieDetails? = arguments?.getParcelable(MOVIEDETAILS)
        renderMovieDetails(movieDetails)

        binding.previous.setOnClickListener {
            findNavController().navigate(R.id.action_MoviesDetailsFragment_to_MoviesListFragment)
        }
    }

    private fun renderMovieDetails(movieDetails: MyMovieDetails?) {
        binding.movieTitle.text = movieDetails?.title
        binding.movieType.text = movieDetails?.type
        (movieDetails?.posterBitmap)?.let { bitMap ->
            binding.poster.setImageBitmap(bitMap)
        }
        binding.movieRelease.text = movieDetails?.release
        binding.movieLanguage.text = movieDetails?.language
        binding.movieRating.text = movieDetails?.rating
        binding.movieGenre.text = movieDetails?.genre
        binding.movieCountry.text = movieDetails?.country
        binding.moviePlot.text = movieDetails?.plot
        binding.movieActors.text = movieDetails?.actors
        binding.movieBoxOffice.text = movieDetails?.boxOffice
        binding.movieAwards.text = movieDetails?.awards

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}