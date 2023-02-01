package com.example.liebmovies.adapters

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.liebmovies.databinding.RecyclerviewRowitemBinding
import com.example.liebmovies.domains.MyMoviesData
import java.util.*

interface ExtraMovieData {
    fun bitMapUpdater(position: Int, posterBitmap: Bitmap? = null)
}

class RecyclerViewAdapter(
    private val defaultPosterImage: Bitmap?,
    private val getMovieData: (movieData: MyMoviesData?) -> Unit,
    private val showSelectedMovie: (String, Bitmap?, String?, String?) -> Unit
) : ExtraMovieData, Filterable, RecyclerView.Adapter<RecyclerViewAdapter.MovieViewHolder>() {

    var listData = ArrayList<MyMoviesData>()
    var unFilteredListData = ArrayList<MyMoviesData>()
    private var isFromLocalStorage: Boolean = false
    fun setUpdatedData(listData: ArrayList<MyMoviesData>, isFromLocalStorage: Boolean) {
        this.isFromLocalStorage = isFromLocalStorage
        this.listData = listData
        this.unFilteredListData = listData
        notifyDataSetChanged()
    }

    class MovieViewHolder(
        binding: RecyclerviewRowitemBinding,
        showSelectedMovie: (String, Bitmap?, String?, String?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.movieTitle
        private val year = binding.movieYear
        val type = binding.movieType
        val poster = binding.poster

        var imbdId: String? = null

        fun bind(
            data: MyMoviesData?,
            position: Int,
            defaultPosterImage: Bitmap?,
            recyclerViewAdapterInterface: ExtraMovieData,
            isFromLocalStorage: Boolean
        ) {// view the specified data on the recycler view
            title.text = data?.title
            year.text = data?.year
            type.text = data?.type

            if (data?.posterUrl != null && data.posterUrl != "N/A") {
                // only the images that come from api are saved
                Glide.with(poster).asBitmap().load(data.posterUrl)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onResourceReady(
                            remotePoster: Bitmap,
                            transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                        ) {
                            poster.setImageBitmap(remotePoster)
                            recyclerViewAdapterInterface.bitMapUpdater(position, remotePoster)
                        }
                    })
            } else {
                (data?.posterBitmap)?.let {
                    // for the local database images coming with bitmaps
                    poster.setImageBitmap(it)
                } ?: run {
                    // the movies that don't have images
                    poster.setImageBitmap(defaultPosterImage)
                    if (!isFromLocalStorage) {
                        recyclerViewAdapterInterface.bitMapUpdater(position, null)
                    }
                }
            }
            imbdId = data?.imdbId
        }

        init {
            itemView.setOnClickListener {
                imbdId?.let { imbdId ->
                    showSelectedMovie.invoke(
                        imbdId, poster.drawable.toBitmap(), title.text.toString(), type.text.toString()
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {

        val binding =
            RecyclerviewRowitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MovieViewHolder(binding, showSelectedMovie)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(listData[position], position, defaultPosterImage, this, isFromLocalStorage)

    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun bitMapUpdater(position: Int, posterBitmap: Bitmap?) {
        if (posterBitmap == null) {
            getMovieData.invoke(listData[position])
        } else {
            if(listData.size>0) {
                listData[position].posterBitmap = posterBitmap
                getMovieData.invoke(listData[position])
            }
        }
    }

    @Suppress("UNREACHABLE_CODE")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterResults = FilterResults()
                if (charSequence.isEmpty()) {
                    filterResults.count = unFilteredListData.size
                    filterResults.values = unFilteredListData
                } else {
                    val searchChr = charSequence.toString().lowercase(Locale.getDefault())
                    val resultData: MutableList<MyMoviesData> = ArrayList<MyMoviesData>()

                    for (moviesData in unFilteredListData) {

                        if (moviesData.title?.lowercase()?.contains(searchChr) == true) {
                            resultData.add(moviesData)
                        }
                    }
                    filterResults.count = resultData.size
                    filterResults.values = resultData
                }
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                listData = results?.values as ArrayList<MyMoviesData>
                notifyDataSetChanged()
            }
        }
    }
}
