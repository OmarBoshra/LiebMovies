package com.example.liebmovies.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.liebmovies.R
import com.example.liebmovies.activities.MoviesActivity
import com.example.liebmovies.adapters.RecyclerViewAdapter
import com.example.liebmovies.customwidgets.MoviesProgressBar
import com.example.liebmovies.databinding.FragmentMoviesListBinding
import com.example.liebmovies.extensions.fadeIn
import com.example.liebmovies.extensions.fadeOut
import com.example.liebmovies.commons.ClickedMovieParams
import com.example.liebmovies.domains.MyMovieDetails
import com.example.liebmovies.domains.MyMoviesData
import com.example.liebmovies.viewmodels.MoviesViewModel
import com.google.android.material.snackbar.Snackbar


class MoviesListFragment : Fragment() {

    private var _binding: FragmentMoviesListBinding? = null
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var dialogProgress: MoviesProgressBar


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var moviesViewModel: MoviesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMoviesListBinding.inflate(inflater, container, false)
        _binding?.fragment = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeSearchText()
        initializeProgressDialog()
        initViewModel()
        initAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initAdapter() {
        val defaultPosterImage = AppCompatResources.getDrawable(
            requireContext(), android.R.drawable.presence_video_online
        )
        // declaring recyclerView adapter
        if (defaultPosterImage != null) {
            recyclerViewAdapter =
                RecyclerViewAdapter(defaultPosterImage.toBitmap(), getMovieData = { movieData ->
                    // save or update locally
                    moviesViewModel.insertingMoviesRequest(
                        movieData, getValidSearchToken(), requireContext()
                    )
                }, showSelectedMovie = { imdbId, posterBitmap, title, type ->

                    // saving them in case of network failure
                    ClickedMovieParams.imdbId = imdbId
                    ClickedMovieParams.title = title
                    ClickedMovieParams.posterImage = posterBitmap
                    ClickedMovieParams.type = type

                    // save the search token to retrive the data later
                    saveSearchToken(getValidSearchToken())

                    showProgressDialog()
                    // call the movie details use case
                    moviesViewModel.getMovieDetails(
                        imdbId, posterBitmap, title, type, getString(
                            R.string.api_key
                        )
                    )

                })
        }
        binding.moviesRecyclerView.adapter = recyclerViewAdapter
    }

    private fun initViewModel() {
        val errorMessage = getString(R.string.errorMessage)

        moviesViewModel = ViewModelProvider(this)[MoviesViewModel::class.java]
        (requireActivity() as MoviesActivity).retroComponent.inject(moviesViewModel)
        _binding?.moviesViewModel = moviesViewModel
        _binding?.lifecycleOwner = this
        // region for movie list
        moviesViewModel.liveMovieCount.observe(viewLifecycleOwner) { _ ->
        }

        moviesViewModel.liveSearchText.observe(viewLifecycleOwner) { char ->
            recyclerViewAdapter.filter.filter(char)
        }

        // region for movie list
        moviesViewModel.liveMyMoviesDataList.observe(viewLifecycleOwner) { moviesResponse ->
            successfulMoviesListRetrieval(moviesResponse, errorMessage)
        }
        moviesViewModel.liveMoviesDataListFailure.observe(viewLifecycleOwner) { failureResponse ->
            failedMoviesListRetrieval(failureResponse, errorMessage)
        }

        moviesViewModel.liveMyMoviesDataListLocal.observe(viewLifecycleOwner) { moviesResponse ->

            if (moviesResponse != null) {
                progressDialogSuccess()

                val numberOfResults = moviesResponse.size
                Snackbar.make(
                    binding.moviesRecyclerView,
                    "$numberOfResults results received",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()

                // add the values to local database when reaching the last movie
                recyclerViewAdapter.setUpdatedData(moviesResponse, true)

            } else {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        moviesViewModel.liveMoviesDataListLocalFailure.observe(viewLifecycleOwner) { failureResponse ->

            if (failureResponse != null) {
                // get from local database
                progressDialogFailure()

            } else {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // region end

        // region for movie details
        moviesViewModel.liveMovieDetails.observe(viewLifecycleOwner) { movieResponse ->
            successfulMovieDetailsRetrieval(movieResponse, errorMessage)
        }
        moviesViewModel.liveMovieDetailsFailure.observe(viewLifecycleOwner) { failureResponse ->
            failedMovieDetailsRetrieval(failureResponse, errorMessage)
        }

        moviesViewModel.liveMovieDetailsLocal.observe(viewLifecycleOwner) { movieResponse ->

            if (movieResponse != null) {

                progressDialogSuccess()

                // send data to the new fragment
                val bundle = Bundle()
                bundle.putParcelable(
                    "MovieDetails", movieResponse
                )

                findNavController().navigate(
                    R.id.action_MoviesListFragment_to_MoviesDetailsFragment, bundle
                )

            } else {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        moviesViewModel.liveMovieDetailsLocalFailure.observe(viewLifecycleOwner) { failureResponse ->

            if (failureResponse != null) {
                // get from local database
                progressDialogFailure()

            } else {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        moviesViewModel.getMovies(
            getValidSearchToken(), getString(
                R.string.api_key
            )
        )
    }

    // region post response methods
    // these methods where created to reduce the cognitive complexity metric
    // in the initViewModel method

    private fun successfulMoviesListRetrieval(
        moviesResponse: ArrayList<MyMoviesData>?, errorMessage: String
    ) {
        if (moviesResponse != null) {
            progressDialogSuccess()

            val numberOfResults = moviesResponse.size
            Snackbar.make(
                binding.moviesRecyclerView,
                "$numberOfResults results received",
                Snackbar.LENGTH_LONG
            ).setAction("Action", null).show()

            // add the values to local database when reaching the last movie
            recyclerViewAdapter.setUpdatedData(moviesResponse, false)

        } else {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun successfulMovieDetailsRetrieval(
        movieResponse: MyMovieDetails?, errorMessage: String
    ) {
        if (movieResponse != null) {

            progressDialogSuccess()

            // insert locally to local db
            moviesViewModel.insertingMoviesDetails(
                ClickedMovieParams.imdbId, movieResponse, requireContext()
            )

            // send data to the new fragment
            val bundle = Bundle()
            bundle.putParcelable(
                "MovieDetails", movieResponse
            )

            findNavController().navigate(
                R.id.action_MoviesListFragment_to_MoviesDetailsFragment,
                bundle,
            )

        } else {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }


    private fun failedMoviesListRetrieval(failureResponse: String?, errorMessage: String) {

        if (failureResponse != null) {
            // get from local database
            progressDialogLocalStorage()

            // try to fetch from local storage
            moviesViewModel.getMoviesListFromLocalStorage(requireContext(),getValidSearchToken())

        } else {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun failedMovieDetailsRetrieval(failureResponse: String?, errorMessage: String) {
        if (failureResponse != null) {
            // get from local database
            progressDialogLocalStorage()
            // try to fetch from local storage
            moviesViewModel.getMovieDetailsFromLocalStorage(
                ClickedMovieParams, requireContext()
            )

        } else {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    // region search button

    // if valid search token send it else use the default one
    private fun getValidSearchToken(): String {
        // if valid search token send it else use the default one
        var searchToken = getString(R.string.default_search_token)
        if (binding.searchText.text.toString().length >= 3) {
            searchToken = binding.searchText.text.toString()
        }
        return searchToken
    }

    private fun saveSearchToken(searchToken : String) {
        val sharedPreferences = context?.getSharedPreferences(getString(R.string.user_preferences), Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString(getString(R.string.saved_search_token_key), searchToken)
        editor?.apply()
    }

    private fun getSavedSearchToken() : String? {
        val sharedPreferences = context?.getSharedPreferences(getString(R.string.user_preferences), Context.MODE_PRIVATE)
        val value = sharedPreferences?.getString(getString(R.string.saved_search_token_key), getString(R.string.default_search_token))
        return value
    }
    private fun initializeSearchText() {
        binding.searchText.setText(getSavedSearchToken())
    }

    // Handle the search button click event
    fun startSearch() {
        showProgressDialog()
        moviesViewModel.getMovies(getValidSearchToken(), getString(R.string.api_key))
    }
    // end region

    // region progress dialog
    private fun initializeProgressDialog() {
        dialogProgress = MoviesProgressBar(requireContext())
        dialogProgress.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogProgress.setCanceledOnTouchOutside(false)
        dialogProgress.show()
    }

    private fun showProgressDialog() {
        dialogProgress.setLoading()
        dialogProgress.getRootView().fadeIn(dialog = dialogProgress)
    }

    private fun progressDialogSuccess() {
        dialogProgress.setSuccess()
        dialogProgress.getRootView().fadeOut(dialog = dialogProgress)
    }

    private fun progressDialogLocalStorage() {
        dialogProgress.getRootView().fadeIn(dialog = dialogProgress)
        dialogProgress.setGetFromLocalStorage()
    }

    private fun progressDialogFailure() {
        dialogProgress.setFailure()
        dialogProgress.getRootView().fadeOut(dialog = dialogProgress)
    }

// end region
}