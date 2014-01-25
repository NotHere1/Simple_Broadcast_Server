package dengine.dischat;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PublicRoomFragment extends Fragment {

	
	public static final String ARG_SECTION_NUMBER = "section_number";

    public PublicRoomFragment() {
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_public_room_fragment, container, false);
        TextView dummyTextView = (TextView) rootView.findViewById(R.id.testing);
        dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

}
