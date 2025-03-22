package com.example.bread.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
<<<<<<< HEAD
=======
import java.util.Comparator;
>>>>>>> 1ae0f4c82f252da1b501fd674f438467590b8038
import java.util.List;

/**
 * Represents a participant in the app, containing user profile information such as username,
 * email, first name, last name, and profile picture. Implements {@link Serializable} to allow
 * easy storage and retrieval from the database.
 */
<<<<<<< HEAD
@IgnoreExtraProperties
public class Participant implements Serializable {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private int followerCount;
    private int followingCount;
=======
public class ProfileFragment extends Fragment {
>>>>>>> 1ae0f4c82f252da1b501fd674f438467590b8038

    @Exclude
    private List<String> followers;
    @Exclude
    private List<String> following;
    @Exclude
    private List<FollowRequest> followRequests;

<<<<<<< HEAD
    /**
     * Default constructor required for Firestore serialization.
     */
    public Participant() {
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.followRequests = new ArrayList<>();
        this.followerCount = 0;
        this.followingCount = 0;
=======
    private TextView usernameText, followersCountText, followingCountText;
    private ImageView profileImageView;
    private LinearLayout followersLayout, followingLayout, allRequestsLayout;
    private RecyclerView requestsRecyclerView;
    private View recentMoodEventView;
    private TextView emptyRequestsText;
    private TextView emptyMoodText;
    private ImageButton settingsButton;

    private ParticipantRepository participantRepository;
    private MoodEventRepository moodEventRepository;
    private String currentUsername;
    private ListenerRegistration participantListener;

    private FollowRequestAdapter requestAdapter;
    private List<FollowRequest> requestsList = new ArrayList<>();

    private ArrayList<MoodEvent> userMoodEvents = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        participantRepository = new ParticipantRepository();
        moodEventRepository = new MoodEventRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        usernameText = view.findViewById(R.id.profile_username);
        followersCountText = view.findViewById(R.id.followers_count);
        followingCountText = view.findViewById(R.id.following_count);
        profileImageView = view.findViewById(R.id.profile_image);

        followersLayout = view.findViewById(R.id.followers_layout);
        followingLayout = view.findViewById(R.id.following_layout);
        allRequestsLayout = view.findViewById(R.id.all_requests_layout);
        requestsRecyclerView = view.findViewById(R.id.request_recycler_view);
        recentMoodEventView = view.findViewById(R.id.recent_mood_container);
        emptyRequestsText = view.findViewById(R.id.empty_requests_text);
        emptyMoodText = view.findViewById(R.id.empty_mood_text);

        // Initialize settings button from main branch
        settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, new SettingsFragment());
            transaction.commit();
        });

        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUsername = currentUser.getDisplayName();
            usernameText.setText(currentUsername);

            // Show loading state
            followersCountText.setText("...");
            followingCountText.setText("...");

            // Set up follow requests
            setupFollowRequests();

            // Set up recent mood event
            loadRecentMoodEvent();

            // Set up real-time listener for participant data
            setupParticipantListener();
        } else {
            navigateToLogin();
        }

        // Set up click listeners
        followersLayout.setOnClickListener(v -> navigateToFollowersList(ParticipantRepository.ListType.FOLLOWERS));
        followingLayout.setOnClickListener(v -> navigateToFollowersList(ParticipantRepository.ListType.FOLLOWING));
        allRequestsLayout.setOnClickListener(v -> navigateToFollowRequests());

        return view;
    }

    private void setupFollowRequests() {
        // Set up RecyclerView
        requestAdapter = new FollowRequestAdapter(requestsList, new FollowRequestAdapter.RequestActionListener() {
            @Override
            public void onAccept(String requestorUsername, int position) {
                handleAcceptRequest(requestorUsername, position);
            }

            @Override
            public void onDecline(String requestorUsername, int position) {
                handleDeclineRequest(requestorUsername, position);
            }
        }, participantRepository);

        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestsRecyclerView.setAdapter(requestAdapter);

        // Load requests
        loadFollowRequests();
    }

    private void loadFollowRequests() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            updateRequestsVisibility();
            return;
        }

        participantRepository.fetchFollowRequests(currentUsername, requests -> {
            requestsList.clear();

            // Only show up to 3 requests in profile
            int count = Math.min(requests.size(), 3);
            for (int i = 0; i < count; i++) {
                requestsList.add(requests.get(i));
            }

            requestAdapter.notifyDataSetChanged();
            updateRequestsVisibility();
        }, e -> {
            Log.e(TAG, "Error loading follow requests", e);
            updateRequestsVisibility();
        });
    }

    private void updateRequestsVisibility() {
        if (requestsList.isEmpty()) {
            emptyRequestsText.setVisibility(View.VISIBLE);
            requestsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyRequestsText.setVisibility(View.GONE);
            requestsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void handleAcceptRequest(String requestorUsername, int position) {
        participantRepository.acceptFollowRequest(currentUsername, requestorUsername, unused -> {
            // Remove from list and update UI
            if (position < requestsList.size()) {
                requestsList.remove(position);
                requestAdapter.notifyItemRemoved(position);
            }
            updateRequestsVisibility();

            // Show follow back dialog
            showFollowBackDialog(requestorUsername);

        }, e -> {
            Log.e(TAG, "Error accepting follow request", e);
        });
    }

    private void handleDeclineRequest(String requestorUsername, int position) {
        participantRepository.declineFollowRequest(currentUsername, requestorUsername, unused -> {
            if (position < requestsList.size()) {
                requestsList.remove(position);
                requestAdapter.notifyItemRemoved(position);
            }
            updateRequestsVisibility();
        }, e -> {
            Log.e(TAG, "Error declining follow request", e);
        });
    }

    private void showFollowBackDialog(String username) {
        if (getContext() != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Follow Back")
                    .setMessage("Do you want to follow " + username + " back?")
                    .setPositiveButton("Follow", (dialog, which) -> {
                        sendFollowBackRequest(username);
                    })
                    .setNegativeButton("Not Now", null)
                    .setCancelable(true)
                    .show();
        }
    }

    private void sendFollowBackRequest(String username) {
        participantRepository.sendFollowRequest(currentUsername, username, unused -> {
            // Success
        }, e -> {
            Log.e(TAG, "Error sending follow back request", e);
        });
    }

    private void loadRecentMoodEvent() {
        if (currentUsername == null) return;

        DocumentReference participantRef = participantRepository.getParticipantRef(currentUsername);
        moodEventRepository.listenForEventsWithParticipantRef(participantRef, moodEvents -> {
            userMoodEvents.clear();
            userMoodEvents.addAll(moodEvents);

            // Sort by date (newest first) using Comparator.reverseOrder
            userMoodEvents.sort(Comparator.reverseOrder());

            // Display most recent mood event
            updateRecentMoodEvent();

        }, e -> {
            Log.e(TAG, "Failed to fetch mood events", e);
            updateRecentMoodEvent();
        });
    }

    private void updateRecentMoodEvent() {
        if (userMoodEvents.isEmpty()) {
            recentMoodEventView.setVisibility(View.GONE);
            emptyMoodText.setVisibility(View.VISIBLE);
        } else {
            // Show most recent mood event
            MoodEvent recentMood = userMoodEvents.get(0);

            // Update UI with mood event details
            TextView usernameView = recentMoodEventView.findViewById(R.id.textUsername);
            TextView reasonView = recentMoodEventView.findViewById(R.id.textReason);
            TextView dateView = recentMoodEventView.findViewById(R.id.textDate);
            TextView moodView = recentMoodEventView.findViewById(R.id.textMood);
            View cardBackground = recentMoodEventView.findViewById(R.id.moodCard);
            View constraintLayout = recentMoodEventView.findViewById(R.id.homeConstraintLayout);

            usernameView.setText(currentUsername);
            reasonView.setText(recentMood.getReason());
            dateView.setText(recentMood.getTimestamp().toString());
            moodView.setText(com.example.bread.utils.EmotionUtils.getEmoticon(recentMood.getEmotionalState()));

            // Set background color based on emotional state
            int colorResId = com.example.bread.utils.EmotionUtils.getColorResource(recentMood.getEmotionalState());

            // Apply color to the card or constraint layout
            if (cardBackground != null) {
                cardBackground.setBackgroundResource(colorResId);
            } else if (constraintLayout != null) {
                constraintLayout.setBackgroundResource(colorResId);
            }

            recentMoodEventView.setVisibility(View.VISIBLE);
            emptyMoodText.setVisibility(View.GONE);
        }
    }

    private void setupParticipantListener() {
        // Remove any existing listener
        if (participantListener != null) {
            participantListener.remove();
        }

        // Set up real-time listener for participant data
        participantListener = participantRepository.listenForParticipantUpdates(currentUsername, participant -> {
            if (participant != null) {
                updateUI(participant);
            }
        });
>>>>>>> 1ae0f4c82f252da1b501fd674f438467590b8038
    }

    /**
     * Constructs a Participant with the specified details.
     *
     * @param username  the username of the participant
     * @param email     the email address of the participant
     * @param firstName the first name of the participant
     * @param lastName  the last name of the participant
     */
    public Participant(String username, String email, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        this.lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.followRequests = new ArrayList<>();
        this.followerCount = 0;
        this.followingCount = 0;
    }

    /**
     * Returns a string representation of the Participant.
     *
     * @return a string containing the participant's details.
     */
    @NonNull
    @Override
    public String toString() {
        return "Participant{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", followerCount=" + followerCount + '\'' +
                ", followingCount=" + followingCount + '\'' +
                '}';
    }

    /**
     * Gets the username of the participant.
     *
     * @return the username as a String.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the participant.
     *
     * @param username the username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address of the participant.
     *
     * @return the email address as a String.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the participant.
     *
     * @param email the email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the first name of the participant.
     *
     * @return the first name as a String.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the participant.
     *
     * @param firstName the first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the participant.
     *
     * @return the last name as a String.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the participant.
     *
     * @param lastName the last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the list of followers for the participant.
     * This field is excluded from Firestore storage.
     *
     * @return a list of usernames representing followers.
     */
    public List<String> getFollowers() {
        return followers;
    }

    /**
     * Sets the list of followers for the participant and updates the follower count.
     * This field is excluded from Firestore storage.
     *
     * @param followers a list of usernames representing followers.
     */
    public void setFollowers(List<String> followers) {
        this.followers = followers;
        this.followerCount = followers != null ? followers.size() : 0;
    }

    /**
     * Gets the list of users that the participant is following.
     * This field is excluded from Firestore storage.
     *
     * @return a list of usernames representing following users.
     */
    public List<String> getFollowing() {
        return following;
    }

    /**
     * Sets the list of users that the participant is following and updates the following count.
     * This field is excluded from Firestore storage.
     *
     * @param following a list of usernames representing following users.
     */
    public void setFollowing(List<String> following) {
        this.following = following;
        this.followingCount = following != null ? following.size() : 0;
    }

    /**
     * Gets the URL of the participant's profile picture.
     *
     * @return the profile picture URL as a String.
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * Sets the URL of the participant's profile picture.
     *
     * @param profilePicture the profile picture URL to set.
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Gets the list of follow requests for this participant.
     * This field is excluded from Firestore storage.
     *
     * @return List of FollowRequest objects.
     */
    public List<FollowRequest> getFollowRequests() {
        return followRequests;
    }

    /**
     * Sets the list of follow requests.
     * This field is excluded from Firestore storage.
     *
     * @param followRequests List of FollowRequest objects.
     */
    public void setFollowRequests(List<FollowRequest> followRequests) {
        this.followRequests = followRequests;
    }

    /**
     * Gets a formatted display name combining first and last name.
     *
     * @return Formatted display name.
     */
    public String getDisplayName() {
        return capitalize(firstName) + " " + capitalize(lastName);
    }

    /**
     * Gets the follower count.
     *
     * @return Number of followers.
     */
    public int getFollowerCount() {
        return followerCount;
    }

    /**
     * Sets the follower count.
     *
     * @param followerCount The count to set.
     */
    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    /**
     * Gets the following count.
     *
     * @return Number of users being followed.
     */
    public int getFollowingCount() {
        return followingCount;
    }

    /**
     * Sets the following count.
     *
     * @param followingCount The count to set.
     */
    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    /**
     * Helper method to capitalize the first letter of a string.
     *
     * @param input The input string.
     * @return String with first letter capitalized.
     */
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}