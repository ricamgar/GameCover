package com.ricamgar.gamecover.android.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import com.ricamgar.gamecover.android.MainActivity
import com.ricamgar.gamecover.android.R
import kotlinx.android.synthetic.fragment_contact.*

/**
 * Fragment for the contact information
 */
class ContactFragment : Fragment() {

    companion object {

        /**
         * Create a new instance of the fragment
         * @return The new instance of [ContactFragment]
         */
        fun newInstance(): ContactFragment {
            return ContactFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        contact_twitter.setOnClickListener { view ->
            val uriUrl = Uri.parse(getString(R.string.contact_twitter_url))
            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
            startActivity(launchBrowser)
        }

        contact_facebook.setOnClickListener { view ->
            val uriUrl: Uri
            try {
                activity.packageManager.getApplicationInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES)
                uriUrl = Uri.parse(getString(R.string.contact_facebook_link))
            } catch (e: PackageManager.NameNotFoundException) {
                uriUrl = Uri.parse(getString(R.string.contact_facebook_url))
            }

            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
            startActivity(launchBrowser)
        }

        contact_youtube.setOnClickListener { view ->
            val uriUrl = Uri.parse(getString(R.string.contact_youtube_url))
            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
            startActivity(launchBrowser)
        }

        contact_gmail.setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("message/rfc822")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("cliente@mirtech.es"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact")
            intent.putExtra(Intent.EXTRA_TEXT, "")
            startActivity(intent)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.red_base))
            (activity as MainActivity).setSupportActionBar(toolbar)
            val supportActionBar = (activity as MainActivity).supportActionBar
            supportActionBar?.setTitle(R.string.drawer_item_contact)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.findItem(R.id.search_game).setVisible(false)
        menu.removeGroup(R.id.order_group)
    }
}
