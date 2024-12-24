import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    private Context mContext;
    private DatabaseReference requestsRef;
    private List<Request> requestList;
    private FirebaseAuth auth;

    public RequestsAdapter(Context context, DatabaseReference requestsRef) {
        this.mContext = context;
        this.requestsRef = requestsRef;
        this.requestList = new ArrayList<>();
        this.auth = FirebaseAuth.getInstance();

        // Listen for data changes and update the adapter
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String numofpeople = childSnapshot.child("numofpeople").getValue(String.class);
                    String description = childSnapshot.child("description").getValue(String.class);
                    String reqid = childSnapshot.getKey(); // Get the generated key

                    // Retrieve user name using its ID
                    String uid = childSnapshot.child("uid").getValue(String.class);
                    String username = getUserName(uid);

                    // Calculate request number for display
                    int requestNumber = Integer.parseInt(reqid.substring(0, 4));

                    requestList.add(new Request(numofpeople, description, username, requestNumber));
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RequestsAdapter", "Error reading data", error.toException());
            }
        });
    }

    // Function to retrieve user name from Firebase Authentication
    private String getUserName(String uid) {
        // Implement your logic to fetch user name based on uid from Firebase Authentication
        // This could involve querying a separate users node or using caching mechanisms
        // For example:
        // User user = FirebaseAuth.getInstance().getCurrentUser();
        // if (user.getUid().equals(uid)) {
        //     return user.getDisplayName();
        // } else {
        //     // Implement logic for fetching name using uid from user node
        // }

        return "Unknown User"; // Replace with your implementation
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.your_request_item_layout, parent, false); // Replace with your actual layout
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);

        holder.numofpeople.setText(request.getNumofpeople() + " people");
        holder.description.setText(request.getDescription());
        holder.username.setText("Posted by: " + request.getUsername());
        holder.reqNumber.setText("#" + request.getRequestNumber());

        // Set up click listeners or other actions based on your needs
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        private TextView numofpeople, description, username, reqNumber;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            numofpeople = itemView.findViewById(R.id.txt_num_people);
            description = itemView.findViewById(R.id.txt_description);
            username = itemView.findViewById(R.id.txt_username);
            reqNumber = itemView.findViewById(R.id.txt_request_number);
        }
    }
}
