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


public class UserAdapter2 extends RecyclerView.Adapter<UserAdapter2.ViewHolder> {
    private Context mcontext;
    private List<User> musers;
    private boolean online;

    public UserAdapter2(Context mcontext, List<User> musers, boolean online){
        this.musers=musers;
        this.mcontext=mcontext;
        this.online=online;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_itemf,parent,false);
        return new UserAdapter2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = musers.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default"))
            holder.profile_image.setImageResource(R.drawable.dp);
        else
            Glide.with(mcontext).load(user.getImageURL()).into(holder.profile_image);

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
}
