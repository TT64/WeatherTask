package task.weathertask;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ChooseCityDialog extends DialogFragment {

    static ChooseCityDialog getInstance() {
        return new ChooseCityDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.chsDlgTitle))
                .setCancelable(false)
                .setSingleChoiceItems(getResources().getStringArray(R.array.cities), 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity target = (MainActivity) getActivity();
                        switch (which) {
                            case 0:
                                target.setCityId(482283);
                                break;
                            case 1:
                                target.setCityId(499099);
                                break;
                            case 2:
                                target.setCityId(524901);
                                break;
                            case 3:
                                target.setCityId(498817);
                                break;
                            case 4:
                                target.setCityId(491422);
                                break;
                            default:
                                break;
                        }
                        dismiss();
                    }
                })
                .create();
    }
}
