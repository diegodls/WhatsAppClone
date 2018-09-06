package com.diegol.whatsappclone.whatsappclone.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.diegol.whatsappclone.whatsappclone.Fragment.ContatosFragment;
import com.diegol.whatsappclone.whatsappclone.Fragment.ConversasFragment;

public class TabAdapter extends FragmentStatePagerAdapter {

    private String[] titulo_abas = {"CONVERSAS", "CONTATOS"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;


        switch (position) {
            case 0:
                fragment = new ConversasFragment();
                break;
            case 1:
                fragment = new ContatosFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return titulo_abas.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titulo_abas[position];
    }
}
