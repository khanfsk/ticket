package com.example.bread.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

<<<<<<< HEAD
import com.example.bread.R;
import com.example.bread.model.MoodEvent;
import com.example.bread.model.Participant;
import com.example.bread.repository.ParticipantRepository;
import com.example.bread.utils.EmotionUtils;
import com.example.bread.utils.ImageHandler;
import com.example.bread.utils.TimestampUtils;
=======
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
>>>>>>> 1ae0f4c82f252da1b501fd674f438467590b8038

import java.util.ArrayList;

/**
<<<<<<< HEAD
 * Adapter class for the HomeFragment ListView
 */
public class HomeMoodEventArrayAdapter extends MoodEventArrayAdapter {
=======
 * Represents a participant in the app, containing user profile information such as username,
 * email, first name, last name, and profile picture. Implements {@link Serializable} to allow
 * easy storage and retrieval from the database.
 */
@IgnoreExtraProperties
public class Participant implements Serializable {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private int followerCount;
    private int followingCount;
>>>>>>> 1ae0f4c82f252da1b501fd674f438467590b8038

    // Cache for participants should be shared across instances to address the senior's comment
    private static final LruCache<String, Participant> participantCache = new LruCache<>(50);
    private final ParticipantRepository userRepo;

<<<<<<< HEAD
    public HomeMoodEventArrayAdapter(@NonNull Context context, ArrayList<MoodEvent> events) {
        super(context, events);
        userRepo = new ParticipantRepository();
    }

    static class ViewHolder {
        TextView username;
        TextView title;
        TextView date;
        TextView mood;
        TextView socialSituation;
        ImageView profilePic;
        ConstraintLayout eventLayout;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_event_home, parent, false);
            holder = new ViewHolder();
            holder.username = convertView.findViewById(R.id.textUsername);
            holder.title = convertView.findViewById(R.id.textTitle);
            holder.date = convertView.findViewById(R.id.textDate);
            holder.mood = convertView.findViewById(R.id.textMood);
            holder.profilePic = convertView.findViewById(R.id.profile_image_home);
            holder.eventLayout = convertView.findViewById(R.id.homeConstraintLayout);
            holder.socialSituation = convertView.findViewById(R.id.textSocialSituation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
=======
    /**
     * Default constructor required for Firestore serialization.
     */
    public Participant() {
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.followRequests = new ArrayList<>();
        this.followerCount = 0;
        this.followingCount = 0;
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
>>>>>>> 1ae0f4c82f252da1b501fd674f438467590b8038
        }

        MoodEvent moodEvent = getItem(position);
        if (moodEvent != null) {
            // Set background color based on emotional state
            int colorResId = EmotionUtils.getColorResource(moodEvent.getEmotionalState());
            holder.eventLayout.setBackgroundResource(colorResId);

            // Load participant info with caching (the improvement requested by senior)
            loadParticipantInfo(moodEvent, holder);

            // Set other mood event data
            holder.title.setText(moodEvent.getTitle());

            // Use TimestampUtils from main branch but handle null timestamps
            if (moodEvent.getTimestamp() != null) {
                holder.date.setText(TimestampUtils.transformTimestamp(moodEvent.getTimestamp()));
            } else {
                holder.date.setText(""); // Handle null timestamp case
            }

            holder.mood.setText(moodEvent.getEmotionalState().toString().toLowerCase() + " " +
                    EmotionUtils.getEmoticon(moodEvent.getEmotionalState()));

            // Handle social situation display
            if (moodEvent.getSocialSituation() != null && moodEvent.getSocialSituation() != MoodEvent.SocialSituation.NONE) {
                holder.socialSituation.setText(moodEvent.getSocialSituation().toString());
                holder.socialSituation.setVisibility(View.VISIBLE);
            } else {
                holder.socialSituation.setVisibility(View.INVISIBLE);
            }

            // Set click listener
            convertView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onMoodEventClick(moodEvent);
                }
            });
        }

        return convertView;
    }

    /**
     * Loads participant information from cache or network
     * This method addresses the senior's comment about caching and avoiding
     * unnecessary network calls
     *
     * @param moodEvent The mood event containing the participant reference
     * @param holder    The ViewHolder to update with participant data
     */
    private void loadParticipantInfo(MoodEvent moodEvent, ViewHolder holder) {
        if (moodEvent.getParticipantRef() == null) {
            holder.username.setText("Unknown");
            holder.profilePic.setImageResource(R.drawable.ic_baseline_profile_24);
            return;
        }

        String refPath = moodEvent.getParticipantRef().getPath();

        // Try to get from cache first
        Participant cachedParticipant = participantCache.get(refPath);
        if (cachedParticipant != null) {
            // Use cached data
            holder.username.setText(cachedParticipant.getUsername());

            // Set profile picture if available
            String base64Image = cachedParticipant.getProfilePicture();
            if (base64Image != null) {
                holder.profilePic.setImageBitmap(ImageHandler.base64ToBitmap(base64Image));
            } else {
                holder.profilePic.setImageResource(R.drawable.ic_baseline_profile_24);
            }
            return;
        }

        // Set defaults while loading
        holder.username.setText("Loading...");
        holder.profilePic.setImageResource(R.drawable.ic_baseline_profile_24);

        // Not in cache, load from network (Firebase calls are already async)
        userRepo.fetchParticipantByRef(moodEvent.getParticipantRef(), participant -> {
            if (participant != null) {
                // Cache the participant for future use
                participantCache.put(refPath, participant);

                // Update UI with participant data
                holder.username.setText(participant.getUsername());
                String base64Image = participant.getProfilePicture();
                if (base64Image != null) {
                    holder.profilePic.setImageBitmap(ImageHandler.base64ToBitmap(base64Image));
                }
            } else {
                holder.username.setText("Unknown");
            }
        }, e -> {
            holder.username.setText("Unknown");
        });
    }
}