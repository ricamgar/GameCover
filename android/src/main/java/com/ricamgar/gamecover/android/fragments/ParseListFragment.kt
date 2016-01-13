/*
 * Copyright (c) 2016. Make It Real S.L.
 */

package com.ricamgar.gamecover.android.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.ricamgar.gamecover.android.MainActivity
import com.ricamgar.gamecover.android.R
import com.ricamgar.gamecover.android.adapters.ParseListAdapter
import com.ricamgar.gamecover.android.models.GameItem
import com.ricamgar.gamecover.android.models.ParseList
import com.ricamgar.gamecover.android.models.ParseResults
import com.ricamgar.gamecover.android.networking.GCRestAdapter
import com.ricamgar.gamecover.android.networking.GCService
import kotlinx.android.synthetic.fragment_games.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit

/**
 * Created by richi.
 */
class ParseListFragment : Fragment(), SearchView.OnQueryTextListener {

    val TAG = ParseListFragment::class.java.simpleName

    private val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id="

    private var listType: Long? = null
    private var gcService: GCService? = null
    private var adapter: ParseListAdapter? = null
    private var subscriptions: CompositeSubscription? = null

    private val searchTextStream = BehaviorSubject.create<String>()

    companion object {

        private val PARAM_LIST_TYPE = "list_type"
        /**
         * Create a new instance of the fragment

         * @return The new instance of [ParseListFragment]
         */
        fun newInstance(@ParseList.ListType listType: Long): ParseListFragment {
            val fragment = ParseListFragment()
            val bundle = Bundle()
            bundle.putLong(PARAM_LIST_TYPE, listType)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        gcService = GCRestAdapter(context).gcService
        listType = arguments.getLong(PARAM_LIST_TYPE)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_games, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        subscriptions = CompositeSubscription()

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.red_base))
        swipeRefreshLayout.setOnRefreshListener {
            requestList()
        }

        gamesList.layoutManager = LinearLayoutManager(activity)
        gamesList.setHasFixedSize(true)

        adapter = ParseListAdapter(object : ParseListAdapter.ItemClickListener {

            override fun onItemSelected(packageId: String?) {
                val intent = activity.packageManager.getLaunchIntentForPackage(packageId)
                if (intent != null) {
                    activity.startActivity(intent)
                } else {
                    val uriUrl = Uri.parse(PLAY_STORE_URL + packageId)
                    val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
                    activity.startActivity(launchBrowser)
                }
            }

        })
        gamesList.adapter = adapter

        requestList()

        val textSubscription = searchTextStream.
                debounce(500, TimeUnit.MILLISECONDS).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe({
                    adapter!!.filter(it)
                })
        subscriptions?.add(textSubscription)
    }

    fun requestList() {
        swipeRefreshLayout.isRefreshing = true
        val subscription = gcService!!.getGamesList("{\"type\":$listType}", 200)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    listParseResults: ParseResults<GameItem> ->
                    adapter!!.addItems(listParseResults.results)
                    swipeRefreshLayout.isRefreshing = false
                }, {
                    swipeRefreshLayout.isRefreshing = false
                })
        subscriptions?.add(subscription)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            (activity as MainActivity).setSupportActionBar(toolbar)
            val supportActionBar = (activity as MainActivity).supportActionBar
            supportActionBar?.setTitle(getTitle(listType))
            toolbarImage.setImageResource(getImage(listType))
        }
    }

    private fun getImage(@ParseList.ListType listType: Long?): Int {
        when (listType) {
            ParseList.GAMES -> return R.drawable.img_games
            ParseList.EMULATORS -> return R.drawable.img_emulators
            ParseList.VR -> return R.drawable.img_glasses
            else -> return R.drawable.img_drawer
        }
    }

    private fun getTitle(@ParseList.ListType listType: Long?): Int {
        when (listType) {
            ParseList.GAMES -> return R.string.drawer_item_games
            ParseList.EMULATORS -> return R.string.drawer_item_emulators
            ParseList.VR -> return R.string.drawer_item_virtual
            else -> return R.string.drawer_item_games
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        val searchView = menu!!.findItem(R.id.search_game).actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.order_date -> {
                adapter!!.orderByDate()
                return true
            }

            R.id.order_name -> {
                adapter!!.orderByName()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscriptions?.unsubscribe()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        searchTextStream.onNext(newText)
        return false
    }
}
