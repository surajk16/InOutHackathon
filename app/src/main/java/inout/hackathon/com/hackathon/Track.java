package inout.hackathon.com.hackathon;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static inout.hackathon.com.hackathon.Constants.MP;

/**
 * Created by suraj on 07-10-2017.
 */

public class Track extends Fragment {
    Button track;
    TextView text;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onResume() {
        super.onResume();
        Log.d("track","onResume");
        if((MP!=null)&&MP.isPlaying()) MP.pause();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    text.append("\n" + intent.getExtras().get("coordinates"));
                    Log.d("coordinates", "" + intent.getExtras().get("coordinates"));
                }
            };
        }
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

        if (Constants.STATUS.equals("START")) {
            track.setText("STOP");
            track.setBackgroundColor(getResources().getColor(R.color.stop_bg));
            text.setText(getResources().getString(R.string.stop_text));
        } else {
            track.setText("START");
            track.setBackgroundColor(getResources().getColor(R.color.start_bg));
            text.setText(getResources().getString(R.string.start_text));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("track","onDestroy");
        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("track","onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);

        text = rootView.findViewById(R.id.track_text);
        track = rootView.findViewById(R.id.track_btn);

        Constants.STATUS = "STOP";

        runtime_permissions();

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.STATUS.equals("STOP")) {
                    getActivity().startService(new Intent(getActivity(), GPS_Service.class));
                    getActivity().startService(new Intent(getActivity(), OrientationService.class));
                    getActivity().startService(new Intent(getActivity(),DetectService.class));
                    track.setText("STOP");
                    Constants.STATUS = "START";
                    track.setBackgroundColor(getResources().getColor(R.color.stop_bg));
                    text.setText(getResources().getString(R.string.stop_text));
                } else {
                    getActivity().stopService(new Intent(getActivity(), GPS_Service.class));
                    getActivity().stopService(new Intent(getActivity(), OrientationService.class));
                    getActivity().startService(new Intent(getActivity(),DetectService.class));
                    track.setText("START");
                    Constants.STATUS = "STOP";
                    track.setBackgroundColor(getResources().getColor(R.color.start_bg));
                    text.setText(getResources().getString(R.string.start_text));
                }
            }
        });


        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                runtime_permissions();
            }
        }
    }


    private void runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }
}
