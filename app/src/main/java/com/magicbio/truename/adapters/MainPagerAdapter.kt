package com.magicbio.truename.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.magicbio.truename.fragments.CallLogFragment
import com.magicbio.truename.fragments.ContactsFragment

class MainPagerAdapter(private val callLogFragment: CallLogFragment, private val contactsFragment: ContactsFragment, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

//    private val messagesFragment = MessagesFragment()
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> callLogFragment
            //0 -> messagesFragment
            else -> contactsFragment
        }
    }

    override fun getCount(): Int {
        return 2
    }
}