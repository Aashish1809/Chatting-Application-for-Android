package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.MainActivity;
import com.example.chat.MessageActivity;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import P1.Chat;
import P1.User;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mcontext;
    private List<User> musers;
    private boolean online;
    private String lastmessages;

    public UserAdapter(Context mcontext, List<User> musers,boolean online){
        this.musers=musers;
        this.mcontext=mcontext;
        this.online=online;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = musers.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default"))
            holder.profile_image.setImageResource(R.drawable.dp);
        else
            Glide.with(mcontext).load(user.getImageURL()).into(holder.profile_image);
        if(online)
           lastmessage(user.getId(),holder.message);
           else
               holder.message.setVisibility(View.GONE);

        if (online) {
            if (user.getStatus().equals("Online"))
                holder.status.setText(R.string.online);
            else
                holder.status.setText(R.string.offline);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mcontext, MessageActivity.class);
                i.putExtra("userid",user.getId());
                mcontext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView username;
        private ImageView profile_image;
        private TextView status;
        private TextView message;

        public ViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            status = itemView.findViewById(R.id.status);
            message = itemView.findViewById(R.id.message);
        }
    }
    private void lastmessage(final String userid, final TextView message){
        lastmessages = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                Chat chat = snapshot.getValue(Chat.class);
                if(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)||
                        chat.getReceiver().equals(userid)&&chat.getSender().equals(firebaseUser.getUid())){
                    lastmessages = chat.getMessage();
                }
            }
            switch (lastmessages){
                case "default":
                message.setText("No Message");
                break;
                default:
                message.setText(lastmessages);}
            lastmessages="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
