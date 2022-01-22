package com.epfl.esl.tidy.ar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.epfl.esl.tidy.R;
import com.epfl.esl.tidy.databinding.FragmentTidyArBinding;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TidyArFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TidyArFragment extends Fragment  {


    private ArFragment arFragment;
    private CardView arMessage;
    private ViewRenderable cardView;

    private FragmentTidyArBinding binding;


    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.-
    private final Map<AugmentedImage, Node> augmentedImageMap = new HashMap<>();


    public TidyArFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TidyArFragment newInstance(String param1, String param2) {
        TidyArFragment fragment = new TidyArFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentTidyArBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                ShowMessage(view);
            }
        };

        handler.postDelayed(r, 25+
                00);

        arMessage = view.findViewById(R.id.ar_message);
        arFragment = (ArFragment) getChildFragmentManager().findFragmentById(R.id.ux_fragment);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        ViewRenderable.builder().setView(getContext(), R.layout.card_layout).build().thenAccept(renderable -> cardView = renderable);

        return view;
    }


    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame, just return.
        if (frame == null) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    String text = "Detected Image " + augmentedImage.getIndex();
                    //SnackbarHelper.getInstance().showMessage(this, text);
                    break;

                case TRACKING:
                    // Have to switch to UI Thread to update View.
                    arMessage.setVisibility(View.GONE);

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        AnchorNode node = new AnchorNode();
                        node.setAnchor(augmentedImage.createAnchor(augmentedImage.getCenterPose()));
                        node.setParent(arFragment.getArSceneView().getScene());

                        Node cardNode = new Node();
                        cardNode.setParent(node);
                        cardNode.setLocalScale(new Vector3(0.2f,0.2f,0.2f));
                        cardNode.setLocalPosition( new Vector3(0.0f,0.3f,0.0f));
                        cardNode.setRenderable(cardView);

                        augmentedImageMap.put(augmentedImage, node);
                        arFragment.getArSceneView().getScene().addChild(node);
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }

    private void ShowMessage(View view) {
        view.findViewById(R.id.ar_guide).setVisibility(View.VISIBLE);
    }
}