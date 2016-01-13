package com.ricamgar.gamecover.android

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.ricamgar.gamecover.android.fragments.ContactFragment
import com.ricamgar.gamecover.android.fragments.ParseListFragment
import com.ricamgar.gamecover.android.models.ParseList

/**
 * The app main Activity
 */
class MainActivity : AppCompatActivity() {

    private var GAMES_FRAGMENT_TAG = "games"
    private var VIRTUAL_REALITY_FRAGMENT_TAG = "virtualReality"
    private var EMULATORS_FRAGMENT_TAG = "emulators"
    private var CONTACT_FRAGMENT_TAG = "contact"

    private var drawerLayout: DrawerLayout? = null

    private var gamesFragment: ParseListFragment? = null
    private var virtualRealityFragment: ParseListFragment? = null
    private var emulatorsFragment: ParseListFragment? = null
    private var contactFragment: ContactFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create fragments if needed; fragment manager might (still) have fragment instances
        gamesFragment = supportFragmentManager.findFragmentByTag(GAMES_FRAGMENT_TAG) as? ParseListFragment
        if (gamesFragment == null) {
            gamesFragment = ParseListFragment.newInstance(ParseList.GAMES)
            supportFragmentManager.beginTransaction().add(R.id.content_layout, gamesFragment, GAMES_FRAGMENT_TAG).commit()
        }

        virtualRealityFragment = supportFragmentManager.findFragmentByTag(VIRTUAL_REALITY_FRAGMENT_TAG) as? ParseListFragment
        if (virtualRealityFragment == null) {
            virtualRealityFragment = ParseListFragment.newInstance(ParseList.VR)
            supportFragmentManager.beginTransaction().add(R.id.content_layout, virtualRealityFragment, VIRTUAL_REALITY_FRAGMENT_TAG).commit()
        }

        emulatorsFragment = supportFragmentManager.findFragmentByTag(EMULATORS_FRAGMENT_TAG) as? ParseListFragment
        if (emulatorsFragment == null) {
            emulatorsFragment = ParseListFragment.newInstance(ParseList.EMULATORS)
            supportFragmentManager.beginTransaction().add(R.id.content_layout, emulatorsFragment, EMULATORS_FRAGMENT_TAG).commit()
        }

        contactFragment = supportFragmentManager.findFragmentByTag(CONTACT_FRAGMENT_TAG) as? ContactFragment
        if (contactFragment == null) {
            contactFragment = ContactFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.content_layout, contactFragment, CONTACT_FRAGMENT_TAG).commit()
        }

        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        drawerLayout!!.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)

        val navigationView = findViewById(R.id.navigation_view) as NavigationView
        navigationView.setNavigationItemSelectedListener { item ->
            item.setChecked(true)
            onSelectedDrawerItem(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            onSelectedDrawerItem(R.id.navigation_games)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_main, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.search_game)
        val searchView = searchItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onSelectedDrawerItem(itemId: Int) {
        val ft = supportFragmentManager.beginTransaction()
        ft.hide(gamesFragment)
        ft.hide(virtualRealityFragment)
        ft.hide(emulatorsFragment)
        ft.hide(contactFragment)

        when (itemId) {
            R.id.navigation_games -> ft.show(gamesFragment)

            R.id.navigation_virtual -> ft.show(virtualRealityFragment)

            R.id.navigation_emulators -> ft.show(emulatorsFragment)

            R.id.navigation_contact -> ft.show(contactFragment)
        }

        ft.commit()
        supportInvalidateOptionsMenu()
        drawerLayout!!.closeDrawers()
    }

}
