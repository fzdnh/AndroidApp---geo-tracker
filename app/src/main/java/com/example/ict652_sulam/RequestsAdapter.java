package com.example.ict652_sulam;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    Context mContext;
    DatabaseReference requestsRef;
    ArrayList<Request>requestList=new ArrayList<>();
    FirebaseAuth auth;
    FirebaseUser user;

    private OnRequestDeletedListener onRequestDeletedListener;

    public RequestsAdapter(Context context, int singledata, ArrayList<Request> requestList, DatabaseReference requestsRef) {
        this.mContext = context;
        this.requestsRef = requestsRef;
        this.requestList = requestList;
    }

    public void setOnRequestDeletedListener(OnRequestDeletedListener listener) {
        this.onRequestDeletedListener = listener;
    }

    private void getUserName(String uid, final UserNameCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child("userid");

        // Use orderByChild and equalTo to find the user with the given UID
        usersRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // If the user is found, retrieve the username
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String username = userSnapshot.child("username").getValue(String.class);
                        if (callback != null) {
                            callback.onUserNameReceived(username);
                        }
                        return; // Break out of the loop after finding the user
                    }
                } else {
                    // If the user is not found, return "Unknown User"
                    if (callback != null) {
                        callback.onUserNameReceived("Unknown User");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", "Error fetching username: " + error.getMessage());
                // Handle error if needed
            }
        });
    }


    public interface UserNameCallback {
        void onUserNameReceived(String username);
    }

    @NonNull
    @Override
    public RequestsAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.singledata, null);
        return new RequestViewHolder(view);
    }
    // Function to retrieve user name from Firebase Authentication

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        final Request model = requestList.get(position);

        holder.numofpeople.setText(model.getNumofpeople());
        holder.description.setText(model.getDescription());
        holder.address.setText(model.getAddress());
        holder.time.setText(model.getTime());

        // Retrieve user name using its ID
        String uid = model.getUid();

        // Call getUserName directly to update the username TextView
        getUserName(uid, new UserNameCallback() {
            @Override
            public void onUserNameReceived(String username) {
                holder.username.setText(username);
            }
        });

        // Assuming the logged-in user's ID is available
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Conditionally show/hide edit and delete buttons based on user ID
        if (user != null && user.getUid().equals(model.getUid()) || user != null && user.getUid().equals("ygNoqavGgJZGEfDsHnPfGDZKj2Y2")) {
            // Show buttons only if the current user is the creator of the request
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);

            // Set up click listeners for edit and delete buttons
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the data you want to edit
                    Bundle bundle = new Bundle();
                    bundle.putString("numofpeople", model.getNumofpeople());
                    bundle.putString("description",model.getDescription());
                    bundle.putString("latitude",model.getLattitude());
                    bundle.putString("longitude",model.getLongitude());
                    bundle.putString("address",model.getAddress());
                    bundle.putString("reqIdToEdit",model.getReqid());
                    Intent intent = new Intent(mContext,SubmitRequest.class);
                    intent.putExtra("requestdata",bundle);
                    mContext.startActivity(intent);
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getAdapterPosition();

                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < requestList.size()) {
                        // Valid position, proceed with deletion
                        String reqIdToDelete = requestList.get(adapterPosition).getReqid();

                        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference().child("requests").child("reqid");

                        requestsRef.child(reqIdToDelete).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // The request is successfully deleted from Firebase, now remove it locally
                                        Toast.makeText(mContext, "Request deleted successfully!", Toast.LENGTH_SHORT).show();

                                        // Find the position of the item to remove
                                        int position = requestList.indexOf(model);

                                        if (position != -1) {
                                            requestList.remove(position);
                                            notifyItemRemoved(position);
                                        } else {
                                            Log.e("RequestsAdapter", "Item not found in the list");
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("RequestsAdapter", "Error deleting request from Firebase", e);
                                        Toast.makeText(mContext, "Failed to delete request", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Handle invalid position
                        Toast.makeText(mContext, "Invalid position for deletion", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } else {
            // Hide buttons if the current user is not the creator of the request
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
    }

    public void updateData(ArrayList<Request> newData) {
        requestList.clear();
        requestList.addAll(newData);
        notifyDataSetChanged();
    }

    public interface OnRequestDeletedListener {
        void onRequestDeleted();
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
    public static class RequestViewHolder extends RecyclerView.ViewHolder {


        private TextView numofpeople, description, username, reqNumber, address, time;
//        Button edit, delete;
        Button edit,delete;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            numofpeople = itemView.findViewById(R.id.txtvictim);
            description = itemView.findViewById(R.id.textdesc);
            username = itemView.findViewById(R.id.txtreq);
            address = itemView.findViewById(R.id.txtaddress);
//            reqNumber = itemView.findViewById(R.id.txt_request_number);
            edit=(Button) itemView.findViewById(R.id.editbtn);
            delete=(Button) itemView.findViewById(R.id.deletebtn);
            time = itemView.findViewById(R.id.txttime);
        }
    }
}
