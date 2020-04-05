package Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import P1.Chat;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int MSG_LEFT =0;
    private static final int MSG_RIGHT =1;
    private Context mcontext;
    private List<Chat> mchat;
    private String imageURL;

    public MessageAdapter(Context mcontext, List<Chat> mchat, String imageURL){
        this.mchat=mchat;
        this.mcontext=mcontext;
        this.imageURL=imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if(viewType==MSG_RIGHT){
        View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right,parent,false);
        return new MessageAdapter.ViewHolder(view);
    }
       else
       {
           View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left,parent,false);
           return new MessageAdapter.ViewHolder(view);
       }
    }


    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
            Chat chat =mchat.get(position);
            holder.show_message.setText(chat.getMessage());
            if(imageURL.equals("default"))
                holder.profile_image.setImageResource(R.drawable.dp);
            else
                Glide.with(mcontext).load(imageURL).into(holder.profile_image);
            if(position == mchat.size()-1){
                if(chat.isIsseen())
                    holder.isseen.setText("Seen");
                else
                    holder.isseen.setText("Delivered");
            }
            else
                holder.isseen.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView isseen;

        public ViewHolder(View itemView){
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            isseen = itemView.findViewById(R.id.isseen);
        }

    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mchat.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_RIGHT;
        else
            return MSG_LEFT;
    }
}