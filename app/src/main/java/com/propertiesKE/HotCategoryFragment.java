package com.propertiesKE;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HotCategoryFragment extends Fragment {
    TextView apartment, bungalow, commercial_building, go_down, holiday_rental, house,
            land, maisonette, office, plot, shop, short_term_rental, studio, villa;
    int Pid;

    public HotCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hot_category, container, false);

        apartment = view.findViewById(R.id.apartment);
        bungalow = view.findViewById(R.id.bungalow);
        commercial_building = view.findViewById(R.id.commercial_building);
        go_down = view.findViewById(R.id.go_down);
        holiday_rental = view.findViewById(R.id.holiday_rental);
        house = view.findViewById(R.id.house);
        land = view.findViewById(R.id.land);
        maisonette = view.findViewById(R.id.maisonette);
        office = view.findViewById(R.id.office);
        plot = view.findViewById(R.id.plot);
        shop = view.findViewById(R.id.shop);
        short_term_rental = view.findViewById(R.id.short_term_rental);
        studio = view.findViewById(R.id.studio);
        villa = view.findViewById(R.id.villa);

        Pid = 10;
        new CheckNumberApartment().execute(new ApiConnector());

        apartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApartmentFragment fragment = new ApartmentFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        bungalow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BungalowFragment fragment = new BungalowFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        commercial_building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommercialBuildingFragment fragment = new CommercialBuildingFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        go_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GodownFragment fragment = new GodownFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        holiday_rental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HolidayRentalFragment fragment = new HolidayRentalFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        house.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HouseFragment fragment = new HouseFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LandFragment fragment = new LandFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        maisonette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaisonetteFragment fragment = new MaisonetteFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfficeFragment fragment = new OfficeFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlotFragment fragment = new PlotFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopFragment fragment = new ShopFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        short_term_rental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShortTermFragment fragment = new ShortTermFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        studio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudioFragment fragment = new StudioFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        villa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VillaFragment fragment = new VillaFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Content, fragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberApartment extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                apartment.setText("Apartment   (" + response + ")");
                Pid = 11;
                new CheckNumberBungalow().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberBungalow extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                bungalow.setText("Bungalow   (" + response + ")");
                Pid = 3;
                new CheckNumberCommercial().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberCommercial extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                commercial_building.setText("Commercial Building   (" + response + ")");
                Pid = 4;
                new CheckNumberGo().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberGo extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                go_down.setText("Go-Down   (" + response + ")");
                Pid = 14;
                new CheckNumberHoliday().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberHoliday extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                holiday_rental.setText("Holiday Rental   (" + response + ")");
                Pid = 8;
                new CheckNumberHouse().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberHouse extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                house.setText("House   (" + response + ")");
                Pid = 1;
                new CheckNumberLand().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberLand extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                land.setText("Land   (" + response + ")");
                Pid = 12;
                new CheckNumberMaisonette().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberMaisonette extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                maisonette.setText("Maisonette   (" + response + ")");
                Pid = 6;
                new CheckNumberOffice().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberOffice extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                office.setText("Office   (" + response + ")");
                Pid = 2;
                new CheckNumberPlot().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberPlot extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                plot.setText("Plot   (" + response + ")");
                Pid = 5;
                new CheckNumberShop().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberShop extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                shop.setText("Shop   (" + response + ")");
                Pid = 13;
                new CheckNumberShort().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberShort extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                short_term_rental.setText("Short Term Rental   (" + response + ")");
                Pid = 7;
                new CheckNumberStudio().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberStudio extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                studio.setText("Studio   (" + response + ")");
                Pid = 9;
                new CheckNumberVilla().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberVilla extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckNumber(Pid);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                villa.setText("Villa   (" + response + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
