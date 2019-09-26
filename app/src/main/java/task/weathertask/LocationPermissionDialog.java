package task.weathertask;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

public class LocationPermissionDialog extends DialogFragment {

    static LocationPermissionDialog getInstance() {
        return new LocationPermissionDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(task.weathertask.R.string.locPermDlgTitle))
                .setMessage(getString(task.weathertask.R.string.locPermDlgMsg))
                .setPositiveButton(getString(task.weathertask.R.string.okBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.LOCATION_PERMISSION_CODE);
                    }
                })
                .create();
    }
}
