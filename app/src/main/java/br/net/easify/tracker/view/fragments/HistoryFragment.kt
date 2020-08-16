package br.net.easify.tracker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import br.net.easify.tracker.R
import br.net.easify.tracker.database.model.DbRoute
import br.net.easify.tracker.view.adapters.RouteHistoryAdapter
import br.net.easify.tracker.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {
    private lateinit var viewModel: HistoryViewModel
    private val routesAdapter = RouteHistoryAdapter(arrayListOf())

    private val routesObserver = Observer<List<DbRoute>> { list ->
        list?.let {
            routesAdapter.updateRoutes(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_history, container, false)

        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        viewModel.routes.observe(viewLifecycleOwner, routesObserver)
        viewModel.refresh()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        routesHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = routesAdapter
        }
    }
}