package com.example.shutter.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {


    private final List<Fragment> mFragmentList= new ArrayList<>();
    private final HashMap<Fragment,Integer> mFragments=new HashMap<>(); // if i have fragment object i can get fragment number
    private final HashMap<String , Integer> mFragmentNumbers=new HashMap<>();// if i have name i can get fragment number
    private final HashMap<Integer , String> mFragmentNames=new HashMap<>(); //if i have frag number i can get name


    public SectionStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }


    public void addFragment(Fragment fragment, String fragmentName)
    {
        mFragmentList.add(fragment);
        mFragments.put(fragment,mFragmentList.size()-1);
        mFragmentNumbers.put(fragmentName,mFragmentList.size()-1);
        mFragmentNames.put(mFragmentList.size()-1,fragmentName);
    }

    /*
    * return the fragement with the name @param
    *
    */

    public Integer getFragmentNumber(String fragmentName)
    {

        if(mFragmentNumbers.containsKey(fragmentName))
        {
            return mFragmentNumbers.get(fragmentName);
        }

        return null;
    }


    public Integer getFragmentNumber(Fragment fragment)
    {

        if(mFragmentNumbers.containsKey(fragment))
        {
            return mFragmentNumbers.get(fragment);
        }

        return null;
    }


    public String getFragmentName(Integer fragmentNumber)
    {
        if(mFragmentNames.containsKey(fragmentNumber))
        {
            return mFragmentNames.get(fragmentNumber);
        }
        return null;
    }
}
